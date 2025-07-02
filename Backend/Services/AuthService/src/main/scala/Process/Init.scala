package Process

import Utils.Constants._

import Common.DBAPI._

import cats.effect.IO
import io.circe.generic.auto._

object Init:
  def initDB: IO[Unit] =
    for
      _ <- initSchema(name)

      _ <- writeDB(
        s"""
          | CREATE TABLE IF NOT EXISTS ${name}.${authTableName} (
          |     token VARCHAR NOT NULL PRIMARY KEY,
          |     user_role VARCHAR NOT NULL,
          |     expires_at BIGINT NOT NULL
          | );
        """.stripMargin,
        List()
      )
    yield ()
