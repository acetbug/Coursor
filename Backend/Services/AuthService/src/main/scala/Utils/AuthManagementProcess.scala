package Utils

import Constants._

import Common.DBAPI._
import Common.Object.SqlParameter
import Objects._

import cats.effect.IO
import java.util.UUID
import java.time.Instant

case object AuthManagementProcess:
  def checkAuth(
      token: String,
      userRole: UserRole
  ): IO[Unit] =
    val sql =
      s"""
        | SELECT expires_at
        | FROM ${name}.${authTableName}
        | WHERE token = ? AND user_role = ?;
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
        | INSERT INTO ${name}.${authTableName} (token, user_role, expires_at)
        | VALUES (?, ?, ?);
       """.stripMargin

    val params = List(
      SqlParameter("String", token),
      SqlParameter("String", userRole.toString),
      SqlParameter("Long", expiresAt.toString)
    )

    for _ <- writeDB(sql, params)
    yield auth

  def deleteAuth(
      token: String
  ): IO[Unit] =
    val sql =
      s"""
        | DELETE FROM ${name}.${authTableName}
        | WHERE token = ?;
       """.stripMargin

    val params = List(SqlParameter("String", token))

    for _ <- writeDB(sql, params)
    yield ()
