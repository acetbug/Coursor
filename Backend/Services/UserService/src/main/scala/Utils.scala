import Common.DBAPI._
import Common.API.Planner
import Global.UserService
import Objects._

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._

object Utils:
  val thisService = UserService

  def init: IO[Unit] =
    for
      _ <- initSchema(thisService.schema)
      _ <- writeDB(
        s"""
         |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.userTable} (
         |    id VARCHAR PRIMARY KEY,
         |    password VARCHAR NOT NULL,
         |    name TEXT NOT NULL,
         |    role VARCHAR(10) NOT NULL
         |);
        """.stripMargin,
        List()
      )
    yield ()

  def handleRequest(
      messageName: String,
      requestJson: Json
  ): IO[Json] = messageName match
    case "CheckUserMessage" =>
      Planner.execute[CheckUserMessagePlanner, UserRole](requestJson)
    case "DeleteUserMessage" =>
      Planner.execute[DeleteUserMessagePlanner, Unit](requestJson)
    case "QueryUsersMessage" =>
      Planner.execute[QueryUsersMessagePlanner, List[User]](requestJson)
    case "UpdateUserMessage" =>
      Planner.execute[UpdateUserMessagePlanner, Unit](requestJson)
    case _ =>
      IO.raiseError(
        new Exception(s"Unknown message type: $messageName")
      )

  def checkUser(
      userId: String,
      password: String
  ): IO[UserRole] =
    val sql =
      s"""
       |SELECT role
       |FROM ${thisService.schema}.${thisService.userTable}
       |WHERE id = ? AND password = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("String", userId),
        SqlParameter("String", password)
      )

    for
      jsonOptional <- readDBJsonOptional(sql, params)

      userRole <- jsonOptional match
        case Some(json) =>
          IO(decodeField[UserRole](json, "role"))
        case None =>
          IO.raiseError(new Exception("User not found or invalid credentials"))
    yield userRole

  def deleteUser(
      userId: String
  ): IO[Unit] =
    val sql =
      s"""
       |DELETE FROM ${thisService.schema}.${thisService.userTable}
       |WHERE id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("String", userId)
      )

    for _ <- writeDB(sql, params) yield ()

  def queryUsers(
      userRole: UserRole
  ): IO[List[User]] =
    val sql =
      s"""
       |SELECT *
       |FROM ${thisService.schema}.${thisService.userTable}
       |WHERE role = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("String", userRole.toString)
      )

    for
      rows <- readDBRows(sql, params)

      users =
        rows.map: row =>
          User(
            id = decodeField[String](row, "id"),
            password = decodeField[String](row, "password"),
            name = decodeField[String](row, "name"),
            role = decodeField[UserRole](row, "role")
          )
    yield users

  def updateUser(
      user: User
  ): IO[Unit] =
    val sql =
      s"""
       |SELECT id
       |FROM ${thisService.schema}.${thisService.userTable}
       |WHERE id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("String", user.id)
      )

    for
      jsonOptional <- readDBJsonOptional(sql, params)

      _ <- jsonOptional match
        case Some(_) =>
          val updateSql =
            s"""
             |UPDATE ${thisService.schema}.${thisService.userTable}
             |SET password = ?, name = ?, role = ?
             |WHERE id = ?;
            """.stripMargin

          val updateParams = List(
            SqlParameter("String", user.password),
            SqlParameter("String", user.name),
            SqlParameter("String", user.role.toString),
            SqlParameter("String", user.id)
          )

          writeDB(updateSql, updateParams)

        case None =>
          val insertSql =
            s"""
             |INSERT INTO ${thisService.schema}.${thisService.userTable} (id, password, name, role)
             |VALUES (?, ?, ?, ?);
            """.stripMargin

          val insertParams = List(
            SqlParameter("String", user.id),
            SqlParameter("String", user.password),
            SqlParameter("String", user.name),
            SqlParameter("String", user.role.toString)
          )

          writeDB(insertSql, insertParams)
    yield ()
