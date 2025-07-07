import Common.DBAPI._
import Common.API.Planner
import Global.UserService
import Objects._

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import java.util.UUID
import java.time.Instant

object Utils:
  val thisService = UserService
  val initSql =
    s"""
      |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.authTable} (
      |    token VARCHAR PRIMARY KEY,
      |    user_id VARCHAR NOT NULL
      |        REFERENCES ${thisService.schema}.${thisService.userTable}(id)
      |        ON DELETE CASCADE,
      |    expires_at BIGINT NOT NULL
      |);
      |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.userTable} (
      |    id VARCHAR PRIMARY KEY,
      |    password VARCHAR NOT NULL,
      |    name TEXT NOT NULL,
      |    role VARCHAR(10) NOT NULL
      |);
    """.stripMargin

  def handleRequest(
      messageName: String,
      requestJson: Json
  ): IO[Json] = messageName match
    case "LoginMessage" =>
      Planner.execute[LoginMessagePlanner, UserAuth](requestJson)
    case "LogoutMessage" =>
      Planner.execute[LogoutMessagePlanner, Unit](requestJson)
    case "CheckAuthMessage" =>
      Planner.execute[CheckAuthMessagePlanner, String](requestJson)
    case "CheckUserRoleMessage" =>
      Planner.execute[CheckUserRoleMessagePlanner, Unit](requestJson)
    case "CreateUserMessage" =>
      Planner.execute[CreateUserMessagePlanner, Unit](requestJson)
    case "QueryUsersMessage" =>
      Planner.execute[QueryUsersMessagePlanner, List[User]](requestJson)
    case "UpdateUserPasswordMessage" =>
      Planner.execute[UpdateUserPasswordMessagePlanner, Unit](requestJson)
    case "UpdateUserNameMessage" =>
      Planner.execute[UpdateUserNameMessagePlanner, Unit](requestJson)
    case "DeleteUserMessage" =>
      Planner.execute[DeleteUserMessagePlanner, Unit](requestJson)
    case _ =>
      IO.raiseError(
        new Exception(s"Unknown message type: $messageName")
      )

  def login(
      userId: String,
      password: String
  ): IO[UserAuth] =
    for
      jsonOpt <- readDBJsonOptional(
        s"""
          |SELECT name, role
          |FROM ${thisService.schema}.${thisService.userTable}
          |WHERE id = ? AND password = ?;
        """.stripMargin,
        List(
          SqlParameter("String", userId),
          SqlParameter("String", password)
        )
      )

      userAuth <- jsonOpt match
        case Some(json) =>
          IO:
            UserAuth(
              token = UUID.randomUUID.toString,
              name = decodeField[String](json, "name"),
              role = decodeField[Role](json, "role")
            )
        case None =>
          IO.raiseError(
            new Exception("User not found or wrong password")
          )

      _ <- writeDB(
        s"""
          |INSERT INTO ${thisService.schema}.${thisService.authTable} (token, user_id, expires_at)
          |VALUES (?, ?, ?);
        """.stripMargin,
        List(
          SqlParameter("String", userAuth.token),
          SqlParameter("String", userId),
          SqlParameter("Long", (Instant.now.getEpochSecond + 1800).toString)
        )
      )
    yield userAuth

  def logout(
      token: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |DELETE FROM ${thisService.schema}.${thisService.authTable}
          |WHERE token = ?;
        """.stripMargin,
        List(SqlParameter("String", token))
      )
    yield ()

  def checkAuth(
      token: String,
      role: Role
  ): IO[String] =
    for
      userId <- readDBString(
        s"""
          |SELECT user_id
          |FROM ${thisService.schema}.${thisService.authTable}
          |WHERE token = ? AND expires_at > ?;
        """.stripMargin,
        List(
          SqlParameter("String", token),
          SqlParameter("Long", Instant.now.getEpochSecond.toString)
        )
      )

      _ <- checkUserRole(userId, role)
    yield userId

  def checkUserRole(
      userId: String,
      role: Role
  ): IO[Unit] =
    for
      exists <- readDBBoolean(
        s"""
          |SELECT EXISTS (
          |    SELECT 1
          |    FROM ${thisService.schema}.${thisService.userTable}
          |    WHERE id = ? AND role = ?;
          |);
        """.stripMargin,
        List(
          SqlParameter("String", userId),
          SqlParameter("String", role.toString)
        )
      )

      _ <-
        if exists then IO.unit
        else
          IO.raiseError(
            new Exception(s"User with id $userId does not have role $role")
          )
    yield ()

  def createUser(
      userId: String,
      password: String,
      name: String,
      role: Role
  ): IO[Unit] =
    for
      exists <- readDBBoolean(
        s"""
          |SELECT EXISTS (
          |    SELECT 1
          |    FROM ${thisService.schema}.${thisService.userTable}
          |    WHERE id = ?;
          |);
        """.stripMargin,
        List(SqlParameter("String", userId))
      )

      _ <-
        if exists then
          IO.raiseError(new Exception(s"User with id $userId already exists"))
        else
          writeDB(
            s"""
              |INSERT INTO ${thisService.schema}.${thisService.userTable} (id, password, name, role)
              |VALUES (?, ?, ?, ?);
            """.stripMargin,
            List(
              SqlParameter("String", userId),
              SqlParameter("String", password),
              SqlParameter("String", name),
              SqlParameter("String", role.toString)
            )
          )
    yield ()

  def queryUsers(
      role: Role
  ): IO[List[User]] =
    for
      rows <- readDBRows(
        s"""
          |SELECT id, name
          |FROM ${thisService.schema}.${thisService.userTable}
          |WHERE role = ?;
        """.stripMargin,
        List(
          SqlParameter("String", role.toString)
        )
      )

      users =
        rows.map: row =>
          User(
            id = decodeField[String](row, "id"),
            name = decodeField[String](row, "name")
          )
    yield users

  def updateUserPassword(
      userId: String,
      password: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |UPDATE ${thisService.schema}.${thisService.userTable}
          |SET password = ?
          |WHERE id = ?;
        """.stripMargin,
        List(
          SqlParameter("String", password),
          SqlParameter("String", userId)
        )
      )
    yield ()

  def updateUserName(
      userId: String,
      name: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |UPDATE ${thisService.schema}.${thisService.userTable}
          |SET name = ?
          |WHERE id = ?;
        """.stripMargin,
        List(
          SqlParameter("String", name),
          SqlParameter("String", userId)
        )
      )
    yield ()

  def deleteUser(
      userId: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |DELETE FROM ${thisService.schema}.${thisService.userTable}
          |WHERE id = ?;
        """.stripMargin,
        List(SqlParameter("String", userId))
      )
    yield ()
