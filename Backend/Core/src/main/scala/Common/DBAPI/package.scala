package Common

import Object.{ParameterList, SqlParameter}
import DBAPI.{
  EndTransactionMessage,
  InitSchemaMessage,
  ReadDBRowsMessage,
  ReadDBValueMessage,
  StartTransactionMessage,
  WriteDBListMessage,
  WriteDBMessage
}

import cats.effect.*
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.generic.auto.*
import io.circe.parser.decode
import org.http4s.client.Client

package object DBAPI {

  def startTransaction[A](block: IO[A])(using encoder: Encoder[A]): IO[A] = {
    val startTransactionAction = StartTransactionMessage().send

    def commitOrRollbackAction(result: Either[Throwable, A]): IO[A] =
      result match {
        case Left(exception) =>
          EndTransactionMessage(false).send >> IO.raiseError(exception)
        case Right(value) =>
          EndTransactionMessage(true).send.as(value)
      }

    for {
      _ <- startTransactionAction

      result <- block.attempt

      _ <- IO.println("Step result")

      _ <- result match
        case Left(value)  => IO.pure(value.printStackTrace())
        case Right(value) => IO.println(s"result = ${result}")

      finalResult <- commitOrRollbackAction(result)
    } yield finalResult
  }

  def initSchema(schemaName: String): IO[String] = InitSchemaMessage(
    schemaName
  ).send

  def readDBRows(
      sqlQuery: String,
      parameters: List[SqlParameter]
  ): IO[List[Json]] =
    ReadDBRowsMessage(sqlQuery, parameters).send

  def readDBJson(sqlQuery: String, parameters: List[SqlParameter]): IO[Json] =
    ReadDBRowsMessage(sqlQuery, parameters).send.map(_.head)

  def readDBJsonOptional(
      sqlQuery: String,
      parameters: List[SqlParameter]
  ): IO[Option[Json]] =
    ReadDBRowsMessage(sqlQuery, parameters).send.map(_.headOption)

  def readDBInt(sqlQuery: String, parameters: List[SqlParameter]): IO[Int] =
    for {
      resultParam: String <- ReadDBValueMessage(sqlQuery, parameters).send
      convertedResult = resultParam.toInt
    } yield convertedResult

  def readDBString(
      sqlQuery: String,
      parameters: List[SqlParameter]
  ): IO[String] =
    for {
      resultParam: String <- ReadDBValueMessage(sqlQuery, parameters).send
    } yield resultParam

  def readDBBoolean(
      sqlQuery: String,
      parameters: List[SqlParameter]
  ): IO[Boolean] =
    for {
      resultParam: String <- ReadDBValueMessage(sqlQuery, parameters).send
      convertedResult = resultParam.startsWith("t")
    } yield convertedResult

  def writeDB(sqlQuery: String, parameters: List[SqlParameter]): IO[String] =
    WriteDBMessage(sqlQuery, parameters).send

  def writeDBList(
      sqlQuery: String,
      parameters: List[ParameterList]
  ): IO[String] = WriteDBListMessage(sqlQuery, parameters).send

  def decodeField[T: Decoder](json: Json, field: String): T = {
    json.hcursor
      .downField(snakeToCamel(field))
      .as[T]
      .fold(throw _, value => value)
  }

  def decodeType[T: Decoder](json: Json): T = {
    json.as[T].fold(throw _, value => value)
  }

  def decodeTypeIO[T: Decoder](json: Json): IO[T] = {
    json.as[T].fold(IO.raiseError, IO.pure)
  }

  def decodeType[T: Decoder](st: String): T = {
    decode[T](st).fold(throw _, value => value)
  }

  def decodeTypeIO[T: Decoder](st: String): IO[T] = {
    decode[T](st).fold(IO.raiseError, IO.pure)
  }

  def snakeToCamel(snake: String): String = {
    snake.split("_").toList match {
      case head :: tail =>
        head + tail.map {
          case "id"  => "ID"
          case other => other.capitalize
        }.mkString
      case Nil => ""
    }
  }
}
