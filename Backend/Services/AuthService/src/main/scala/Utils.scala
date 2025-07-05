import Common.DBAPI._
import Common.API.Planner
import Global._
import Objects._

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import java.time.Instant
import java.util.UUID

object Utils:
  val thisService = AuthService

  def init: IO[Unit] =
    for
      _ <- initSchema(thisService.schema)
      _ <- writeDB(
        s"""
         |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.authTable} (
         |    token CHAR(36) PRIMARY KEY,
         |    user_role VARCHAR(10) NOT NULL,
         |    expires_at BIGINT NOT NULL
         |);
        """.stripMargin,
        List()
      )
    yield ()

  def handleRequest(
      messageName: String,
      requestJson: Json
  ): IO[Json] =
    messageName match
      case "CheckAuthMessage" =>
        Planner.execute[CheckAuthMessagePlanner, Unit](requestJson)
      case "CreateAuthMessage" =>
        Planner.execute[CreateAuthMessagePlanner, Auth](requestJson)
      case "DeleteAuthMessage" =>
        Planner.execute[DeleteAuthMessagePlanner, Unit](requestJson)
      case _ =>
        IO.raiseError(
          new Exception(s"Unknown message type: $messageName")
        )

  def checkAuth(
      token: String,
      userRole: UserRole
  ): IO[Unit] =
    val sql =
      s"""
       |SELECT expires_at
       |FROM ${thisService.schema}.${thisService.authTable}
       |WHERE token = ? AND user_role = ?;
      """.stripMargin

    val params = List(
      SqlParameter("String", token),
      SqlParameter("String", userRole.toString)
    )

    for
      jsonOptional <- readDBJsonOptional(sql, params)

      _ <- jsonOptional match
        case Some(json) =>
          val expiresAt = decodeField[Long](json, "expires_at")
          if Instant.now.getEpochSecond > expiresAt then
            IO.raiseError(new Exception("Authentication expired"))
          else IO.unit

        case None =>
          IO.raiseError(
            new Exception("Authentication or authorization not found")
          )
    yield ()

  def createAuth(
      userRole: UserRole
  ): IO[Auth] =
    val token = UUID.randomUUID.toString

    val expiresAt = Instant.now.getEpochSecond + 1800

    val auth = Auth(
      token = token,
      userRole = userRole,
      expiresAt = expiresAt
    )

    val sql =
      s"""
       |INSERT INTO ${thisService.schema}.${thisService.authTable} (token, user_role, expires_at)
       |VALUES (?, ?, ?);
      """.stripMargin

    val params = List(
      SqlParameter("String", token),
      SqlParameter("String", userRole.toString),
      SqlParameter("Long", expiresAt.toString)
    )

    for _ <- writeDB(sql, params) yield auth

  def deleteAuth(
      token: String
  ): IO[Unit] =
    val sql =
      s"""
       |DELETE FROM ${thisService.schema}.${thisService.authTable}
       |WHERE token = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("String", token)
      )

    for _ <- writeDB(sql, params) yield ()
