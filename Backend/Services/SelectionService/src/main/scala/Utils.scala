import Common.DBAPI._
import Common.API.Planner
import Global._
import Objects._

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._

object Utils:
  val thisService = SelectionService

  def init: IO[Unit] =
    for
      _ <- initSchema(thisService.schema)
      _ <- writeDB(
        s"""
         |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.selectionRecordTable} (
         |    id SERIAL PRIMARY KEY,
         |    student_id VARCHAR NOT NULL
         |        REFERENCES ${UserService.schema}.${UserService.userTable}(id)
         |        ON DELETE CASCADE,
         |    term_id INT NOT NULL
         |        REFERENCES ${TermService.schema}.${TermService.termTable}(id)
         |        ON DELETE CASCADE,
         |    course_id INT NOT NULL
         |        REFERENCES ${CourseService.schema}.${CourseService.courseTable}(id)
         |        ON DELETE CASCADE,
         |    points INT NOT NULL,
         |    UNIQUE (term_id, student_id, course_id)
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
      case _ =>
        IO.raiseError(
          new Exception(s"Unknown message type: $messageName")
        )

  def deleteSelectionRecord(
      selectionRecordId: String
  ): IO[Unit] =
    val sql =
      s"""
       |DELETE FROM ${thisService.schema}.${thisService.selectionRecordTable}
       |WHERE id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("Int", selectionRecordId)
      )

    for _ <- writeDB(sql, params) yield ()

  def querySelectionRecords(
      studentId: String,
      termId: String
  ): IO[List[SelectionRecord]] =
    val sql =
      s"""
       |SELECT *
       |FROM ${thisService.schema}.${thisService.selectionRecordTable}
       |WHERE student_id = ? AND term_id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("String", studentId),
        SqlParameter("Int", termId)
      )

    for
      rows <- readDBRows(sql, params)

      selectionRecords =
        rows.map: row =>
          SelectionRecord(
            id = decodeField[Int](row, "id").toString,
            studentId = decodeField[String](row, "student_id"),
            termId = decodeField[Int](row, "term_id").toString,
            courseId = decodeField[Int](row, "course_id").toString,
            points = decodeField[Int](row, "points")
          )
    yield selectionRecords

  def updateSelectionRecord(
      selectionRecord: SelectionRecord
  ): IO[String] =
    val insertSql =
      s"""
       |INSERT INTO ${thisService.schema}.${thisService.selectionRecordTable} (student_id, term_id, course_id, points)
       |VALUES (?, ?, ?, ?)
       |RETURNING id;
      """.stripMargin

    val insertParams =
      List(
        SqlParameter("String", selectionRecord.studentId),
        SqlParameter("Int", selectionRecord.termId),
        SqlParameter("Int", selectionRecord.courseId),
        SqlParameter("Int", selectionRecord.points.toString)
      )

    val updateSql =
      s"""
       |UPDATE ${thisService.schema}.${thisService.selectionRecordTable}
       |SET student_id = ?, term_id = ?, course_id = ?, points = ?
       |WHERE id = ?;
      """.stripMargin

    val updateParams =
      List(
        SqlParameter("String", selectionRecord.studentId),
        SqlParameter("Int", selectionRecord.termId),
        SqlParameter("Int", selectionRecord.courseId),
        SqlParameter("Int", selectionRecord.points.toString),
        SqlParameter("Int", selectionRecord.id)
      )

    if selectionRecord.id.isEmpty then
      for id <- readDBInt(insertSql, insertParams)
      yield id.toString
    else
      for _ <- writeDB(updateSql, updateParams)
      yield selectionRecord.id

  def executeSelection: IO[Unit] =
    
