import Common.DBAPI._
import Common.API.Planner
import Global._
import Objects.Subject

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._

object Utils:
  val thisService = SubjectService

  def init: IO[Unit] =
    for
      _ <- initSchema(thisService.schema)
      _ <- writeDB(
        s"""
         |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.subjectTable} (
         |    id SERIAL PRIMARY KEY,
         |    name VARCHAR NOT NULL,
         |    credits INT NOT NULL
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
      case "DeleteSubjectMessage" =>
        Planner.execute[DeleteSubjectMessagePlanner, Unit](requestJson)
      case "QuerySubjectsMessage" =>
        Planner.execute[QuerySubjectsMessagePlanner, List[Subject]](requestJson)
      case "UpdateSubjectMessage" =>
        Planner.execute[UpdateSubjectMessagePlanner, String](requestJson)
      case _ =>
        IO.raiseError(
          new Exception(s"Unknown message type: $messageName")
        )

  def deleteSubject(
      subjectId: String
  ): IO[Unit] =
    val sql =
      s"""
       |DELETE FROM ${thisService.schema}.${thisService.subjectTable}
       |WHERE id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("Int", subjectId)
      )

    for _ <- writeDB(sql, params) yield ()

  def querySubjects(
      subjectIds: List[String]
  ): IO[List[Subject]] =
    val isAll = subjectIds.isEmpty

    val sql =
      if isAll then s"""
         |SELECT *
         |FROM ${thisService.schema}.${thisService.subjectTable};
        """.stripMargin
      else s"""
         |SELECT *
         |FROM ${thisService.schema}.${thisService.subjectTable}
         |WHERE id IN (${subjectIds.map(_ => "?").mkString(", ")});
        """.stripMargin

    val params: List[SqlParameter] =
      if isAll then Nil
      else subjectIds.map(id => SqlParameter("Int", id))

    for
      rows <- readDBRows(sql, params)

      subjects =
        rows.map: row =>
          Subject(
            id = decodeField[Int](row, "id").toString,
            name = decodeField[String](row, "name"),
            credits = decodeField[Int](row, "credits")
          )
    yield subjects

  def updateSubject(
      subject: Subject
  ): IO[String] =
    val insertSql =
      s"""
       |INSERT INTO ${thisService.schema}.${thisService.subjectTable} (name, credits)
       |VALUES (?, ?)
       |RETURNING id;
      """.stripMargin

    val insertParams =
      List(
        SqlParameter("String", subject.name),
        SqlParameter("Int", subject.credits.toString)
      )

    val updateSql =
      s"""
       |UPDATE ${thisService.schema}.${thisService.subjectTable}
       |SET name = ?, credits = ?
       |WHERE id = ?;
      """.stripMargin

    val updateParams =
      List(
        SqlParameter("String", subject.name),
        SqlParameter("Int", subject.credits.toString),
        SqlParameter("Int", subject.id)
      )

    if subject.id.isEmpty then
      for id <- readDBInt(insertSql, insertParams)
      yield id.toString
    else
      for _ <- writeDB(updateSql, updateParams)
      yield subject.id
