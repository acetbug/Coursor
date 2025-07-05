import Common.DBAPI._
import Common.API.Planner
import Global._
import Objects._

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._

object Utils:
  val thisService = StudyService

  def init: IO[Unit] =
    for
      _ <- initSchema(thisService.schema)
      _ <- writeDB(
        s"""
         |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.studyTraceTable} (
         |    id SERIAL PRIMARY KEY,
         |    student_id VARCHAR NOT NULL
         |        REFERENCES ${UserService.schema}.${UserService.userTable}(id)
         |        ON DELETE CASCADE,
         |    term_id INT NOT NULL
         |        REFERENCES ${TermService.schema}.${TermService.termTable}(id)
         |        ON DELETE CASCADE,
         |    stage VARCHAR(12) NOT NULL,
         |    UNIQUE (student_id, term_id)
         |);
         |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.studyRecordTable} (
         |    id SERIAL PRIMARY KEY,
         |    student_id VARCHAR NOT NULL
         |        REFERENCES ${UserService.schema}.${UserService.userTable}(id)
         |        ON DELETE CASCADE,
         |    subject_id INT NOT NULL
         |        REFERENCES ${SubjectService.schema}.${SubjectService.subjectTable}(id)
         |        ON DELETE CASCADE,
         |    UNIQUE (student_id, subject_id)
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
      case "DeleteStudyTraceMessage" =>
        Planner.execute[DeleteStudyTraceMessagePlanner, Unit](requestJson)
      case "DeleteStudyRecordMessage" =>
        Planner.execute[DeleteStudyRecordMessagePlanner, Unit](requestJson)
      case "QueryStudyTracesMessage" =>
        Planner.execute[QueryStudyTracesMessagePlanner, List[StudyTrace]](
          requestJson
        )
      case "QueryStudyRecordsMessage" =>
        Planner.execute[QueryStudyRecordsMessagePlanner, List[StudyRecord]](
          requestJson
        )
      case "UpdateStudyTraceMessage" =>
        Planner.execute[UpdateStudyTraceMessagePlanner, String](requestJson)
      case "UpdateStudyRecordMessage" =>
        Planner.execute[UpdateStudyRecordMessagePlanner, String](requestJson)
      case _ =>
        IO.raiseError(
          new Exception(s"Unknown message type: $messageName")
        )

  def deleteStudyTrace(
      studyTraceId: String
  ): IO[Unit] =
    val sql =
      s"""
       |DELETE FROM ${thisService.schema}.${thisService.studyTraceTable}
       |WHERE id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("Int", studyTraceId)
      )

    for _ <- writeDB(sql, params) yield ()

  def deleteStudyRecord(
      studyRecordId: String
  ): IO[Unit] =
    val sql =
      s"""
       |DELETE FROM ${thisService.schema}.${thisService.studyRecordTable}
       |WHERE id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("Int", studyRecordId)
      )

    for _ <- writeDB(sql, params) yield ()

  def queryStudyTraces(
      studentId: String
  ): IO[List[StudyTrace]] =
    val sql =
      s"""
       |SELECT *
       |FROM ${thisService.schema}.${thisService.studyTraceTable}
       |WHERE student_id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("String", studentId)
      )

    for
      rows <- readDBRows(sql, params)

      studyTraces =
        rows.map: row =>
          StudyTrace(
            id = decodeField[Int](row, "id").toString,
            studentId = decodeField[String](row, "student_id"),
            termId = decodeField[Int](row, "term_id").toString,
            stage = decodeField[StudyStage](row, "stage")
          )
    yield studyTraces

  def queryStudyRecords(
      studentId: String
  ): IO[List[StudyRecord]] =
    val sql =
      s"""
       |SELECT *
       |FROM ${thisService.schema}.${thisService.studyRecordTable}
       |WHERE student_id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("String", studentId)
      )

    for
      rows <- readDBRows(sql, params)

      studyRecords =
        rows.map(row =>
          StudyRecord(
            id = decodeField[Int](row, "id").toString,
            studentId = decodeField[String](row, "student_id"),
            subjectId = decodeField[Int](row, "subject_id").toString
          )
        )
    yield studyRecords

  def updateStudyTrace(
      studyTrace: StudyTrace
  ): IO[String] =
    val insertSql =
      s"""
       |INSERT INTO ${thisService.schema}.${thisService.studyTraceTable} (student_id, term_id, stage)
       |VALUES (?, ?, ?)
       |RETURNING id;
      """.stripMargin

    val insertParams =
      List(
        SqlParameter("String", studyTrace.studentId),
        SqlParameter("Int", studyTrace.termId),
        SqlParameter("String", studyTrace.stage.toString)
      )

    val updateSql =
      s"""
       |UPDATE ${thisService.schema}.${thisService.studyTraceTable}
       |SET student_id = ?, term_id = ?, stage = ?
       |WHERE id = ?;
      """.stripMargin

    val updateParams =
      List(
        SqlParameter("String", studyTrace.studentId),
        SqlParameter("Int", studyTrace.termId),
        SqlParameter("String", studyTrace.stage.toString),
        SqlParameter("Int", studyTrace.id)
      )

    if studyTrace.id.isEmpty then
      for id <- readDBInt(insertSql, insertParams)
      yield id.toString
    else
      for _ <- writeDB(updateSql, updateParams)
      yield studyTrace.id

  def updateStudyRecord(
      studyRecord: StudyRecord
  ): IO[String] =
    val insertSql =
      s"""
       |INSERT INTO ${thisService.schema}.${thisService.studyRecordTable} (student_id, subject_id)
       |VALUES (?, ?)
       |RETURNING id;
      """.stripMargin

    val insertParams =
      List(
        SqlParameter("String", studyRecord.studentId),
        SqlParameter("Int", studyRecord.subjectId)
      )

    val updateSql =
      s"""
       |UPDATE ${thisService.schema}.${thisService.studyRecordTable}
       |SET student_id = ?, subject_id = ?
       |WHERE id = ?;
      """.stripMargin

    val updateParams =
      List(
        SqlParameter("String", studyRecord.studentId),
        SqlParameter("Int", studyRecord.subjectId),
        SqlParameter("Int", studyRecord.id)
      )

    if studyRecord.id.isEmpty then
      for id <- readDBInt(insertSql, insertParams)
      yield id.toString
    else
      for _ <- writeDB(updateSql, updateParams)
      yield studyRecord.id
