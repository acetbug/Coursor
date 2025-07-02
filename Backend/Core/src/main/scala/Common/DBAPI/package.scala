package Common

import Object._
import DBAPI._

import cats.effect._
import io.circe._
import io.circe.generic.auto._
import io.circe.parser.decode
import org.http4s.client.Client

package object DBAPI:
  def initSchema(schemaName: String): IO[String] =
    InitSchemaMessage(schemaName).send

  def readDBRows(
      sqlQuery: String,
      parameters: List[SqlParameter]
  ): IO[List[Json]] =
    ReadDBRowsMessage(sqlQuery, parameters).send

  def readDBJson(
      sqlQuery: String,
      parameters: List[SqlParameter]
  ): IO[Json] =
    ReadDBRowsMessage(sqlQuery, parameters).send.map(_.head)

  def readDBJsonOptional(
      sqlQuery: String,
      parameters: List[SqlParameter]
  ): IO[Option[Json]] =
    ReadDBRowsMessage(sqlQuery, parameters).send.map(_.headOption)

  def readDBInt(
      sqlQuery: String,
      parameters: List[SqlParameter]
  ): IO[Int] =
    for
      resultParam: String <- ReadDBValueMessage(sqlQuery, parameters).send
      convertedResult = resultParam.toInt
    yield convertedResult

  def readDBString(
      sqlQuery: String,
      parameters: List[SqlParameter]
  ): IO[String] =
    for resultParam: String <- ReadDBValueMessage(sqlQuery, parameters).send
    yield resultParam

  def readDBBoolean(
      sqlQuery: String,
      parameters: List[SqlParameter]
  ): IO[Boolean] =
    for
      resultParam: String <- ReadDBValueMessage(sqlQuery, parameters).send
      convertedResult = resultParam.startsWith("t")
    yield convertedResult

  def writeDB(
      sqlQuery: String,
      parameters: List[SqlParameter]
  ): IO[String] =
    WriteDBMessage(sqlQuery, parameters).send

  def writeDBList(
      sqlQuery: String,
      parameters: List[ParameterList]
  ): IO[String] = WriteDBListMessage(sqlQuery, parameters).send

  def decodeField[T: Decoder](json: Json, field: String): T =
    json.hcursor
      .downField(snakeToCamel(field))
      .as[T]
      .fold(throw _, value => value)

  def decodeType[T: Decoder](json: Json): T =
    json.as[T].fold(throw _, value => value)

  def decodeTypeIO[T: Decoder](json: Json): IO[T] =
    json.as[T].fold(IO.raiseError, IO.pure)

  def decodeType[T: Decoder](st: String): T =
    decode[T](st).fold(throw _, value => value)

  def decodeTypeIO[T: Decoder](st: String): IO[T] =
    decode[T](st).fold(IO.raiseError, IO.pure)

  def snakeToCamel(snake: String): String =
    snake.split("_").toList match
      case head :: tail =>
        head + tail.map(_.capitalize).mkString
      case Nil => ""
