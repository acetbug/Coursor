import Common.DBAPI._
import Common.API.Planner
import Global.DepartmentService
import Objects._

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import Global.SubjectService

object Utils:
  val thisService = DepartmentService

  def init: IO[Unit] =
    for
      _ <- initSchema(thisService.schema)
      _ <- writeDB(
        s"""
         |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.departmentTable} (
         |    id SERIAL PRIMARY KEY,
         |    name VARCHAR UNIQUE NOT NULL
         |);
         |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.departmentSubjectRecommendationTable} (
         |    id SERIAL PRIMARY KEY,
         |    department_id INT NOT NULL
         |        REFERENCES ${DepartmentService.schema}.${DepartmentService.departmentTable}(id)
         |        ON DELETE CASCADE,
         |    subject_id INT NOT NULL
         |        REFERENCES ${SubjectService.schema}.${SubjectService.subjectTable}(id)
         |        ON DELETE CASCADE,
         |    study_stage VARCHAR(12) NOT NULL,
         |    priority INT NOT NULL,
         |    UNIQUE (department_id, subject_id)
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
      case "DeleteDepartmentMessage" =>
        Planner.execute[DeleteDepartmentMessagePlanner, Unit](requestJson)
      case "DeleteDepartmentSubjectRecommendationMessage" =>
        Planner
          .execute[DeleteDepartmentSubjectRecommendationMessagePlanner, Unit](
            requestJson
          )
      case "QueryDepartmentsMessage" =>
        Planner.execute[QueryDepartmentsMessagePlanner, List[Department]](
          requestJson
        )
      case "QueryDepartmentSubjectRecommendationsMessage" =>
        Planner
          .execute[QueryDepartmentSubjectRecommendationsMessagePlanner, List[
            DepartmentSubjectRecommendation
          ]](requestJson)
      case "UpdateDepartmentMessage" =>
        Planner.execute[UpdateDepartmentMessagePlanner, String](requestJson)
      case "UpdateDepartmentSubjectRecommendationMessage" =>
        Planner
          .execute[UpdateDepartmentSubjectRecommendationMessagePlanner, String](
            requestJson
          )
      case _ =>
        IO.raiseError(
          new Exception(s"Unknown message type: $messageName")
        )

  def deleteDepartment(
      departmentId: String
  ): IO[Unit] =
    val sql =
      s"""
       |DELETE FROM ${thisService.schema}.${thisService.departmentTable}
       |WHERE id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("Int", departmentId)
      )

    for _ <- writeDB(sql, params) yield ()

  def deleteDepartmentSubjectRecommendation(
      departmentSubjectRecommendationId: String
  ): IO[Unit] =
    val sql =
      s"""
       |DELETE FROM ${thisService.schema}.${thisService.departmentSubjectRecommendationTable}
       |WHERE id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("Int", departmentSubjectRecommendationId)
      )

    for _ <- writeDB(sql, params) yield ()

  def queryDepartments(
      departmentIds: List[String]
  ): IO[List[Department]] =
    val isAll = departmentIds.isEmpty

    val sql =
      if isAll then s"""
         |SELECT *
         |FROM ${thisService.schema}.${thisService.departmentTable}
        """.stripMargin
      else s"""
         |SELECT *
         |FROM ${thisService.schema}.${thisService.departmentTable}
         |WHERE id IN (${departmentIds.map(_ => "?").mkString(", ")});
        """.stripMargin

    val params: List[SqlParameter] =
      if isAll then Nil
      else departmentIds.map(id => SqlParameter("Int", id))

    for
      rows <- readDBRows(sql, params)

      departments =
        rows.map: row =>
          Department(
            id = decodeField[Int](row, "id").toString,
            name = decodeField[String](row, "name")
          )
    yield departments

  def queryDepartmentSubjectRecommendations(
      departmentId: String,
      studyStage: StudyStage
  ): IO[List[DepartmentSubjectRecommendation]] =
    val sql =
      s"""
       |SELECT *
       |FROM ${thisService.schema}.${thisService.departmentSubjectRecommendationTable}
       |WHERE department_id = ? AND study_stage = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("Int", departmentId),
        SqlParameter("String", studyStage.toString)
      )

    for
      rows <- readDBRows(sql, params)

      departmentSubjectRecommendations =
        rows.map: row =>
          DepartmentSubjectRecommendation(
            id = decodeField[Int](row, "id").toString,
            departmentId = decodeField[Int](row, "department_id").toString,
            subjectId = decodeField[Int](row, "subject_id").toString,
            studyStage = decodeField[StudyStage](row, "study_stage"),
            priority = decodeField[Int](row, "priority")
          )
    yield departmentSubjectRecommendations

  def updateDepartment(
      department: Department
  ): IO[String] =
    val insertSql =
      s"""
       |INSERT INTO ${thisService.schema}.${thisService.departmentTable} (name)
       |VALUES (?)
       |RETURNING id;
      """.stripMargin

    val insertParams =
      List(
        SqlParameter("String", department.name)
      )

    val updateSql =
      s"""
       |UPDATE ${thisService.schema}.${thisService.departmentTable}
       |SET name = ?
       |WHERE id = ?;
      """.stripMargin

    val updateParams =
      List(
        SqlParameter("String", department.name),
        SqlParameter("Int", department.id)
      )

    if department.id.isEmpty then
      for id <- readDBInt(insertSql, insertParams)
      yield id.toString
    else
      for _ <- writeDB(updateSql, updateParams)
      yield department.id

  def updateDepartmentSubjectRecommendation(
      departmentSubjectRecommendation: DepartmentSubjectRecommendation
  ): IO[String] =
    val insertSql =
      s"""
       |INSERT INTO ${thisService.schema}.${thisService.departmentSubjectRecommendationTable} (department_id, subject_id, study_stage, priority)
       |VALUES (?, ?, ?, ?)
       |RETURNING id;
      """.stripMargin

    val insertParams =
      List(
        SqlParameter("Int", departmentSubjectRecommendation.departmentId),
        SqlParameter("Int", departmentSubjectRecommendation.subjectId),
        SqlParameter(
          "String",
          departmentSubjectRecommendation.studyStage.toString
        ),
        SqlParameter("Int", departmentSubjectRecommendation.priority.toString)
      )

    val updateSql =
      s"""
       |UPDATE ${thisService.schema}.${thisService.departmentSubjectRecommendationTable}
       |SET department_id = ?, subject_id = ?, study_stage = ?, priority = ?
       |WHERE id = ?;
      """.stripMargin

    val updateParams =
      List(
        SqlParameter("Int", departmentSubjectRecommendation.departmentId),
        SqlParameter("Int", departmentSubjectRecommendation.subjectId),
        SqlParameter(
          "String",
          departmentSubjectRecommendation.studyStage.toString
        ),
        SqlParameter("Int", departmentSubjectRecommendation.priority.toString),
        SqlParameter("Int", departmentSubjectRecommendation.id)
      )

    if departmentSubjectRecommendation.id.isEmpty then
      for id <- readDBInt(insertSql, insertParams)
      yield id.toString
    else
      for _ <- writeDB(updateSql, updateParams)
      yield departmentSubjectRecommendation.id
