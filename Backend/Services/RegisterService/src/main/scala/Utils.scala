import Common.DBAPI._
import Common.API.Planner
import Global._
import Objects.Register

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._

object Utils:
  val thisService = RegisterService

  def init: IO[Unit] =
    for
      _ <- initSchema(thisService.schema)
      _ <- writeDB(
        s"""
         |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.registerTable} (
         |    id SERIAL PRIMARY KEY,
         |    student_id VARCHAR NOT NULL
         |        REFERENCES ${UserService.schema}.${UserService.userTable}(id)
         |        ON DELETE CASCADE,
         |    department_id INT NOT NULL
         |        REFERENCES ${DepartmentService.schema}.${DepartmentService.departmentTable}(id)
         |        ON DELETE CASCADE,
         |    UNIQUE (student_id, department_id)
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
      case "DeleteRegisterMessage" =>
        Planner.execute[DeleteRegisterMessagePlanner, Unit](requestJson)
      case "QueryRegistersByDepartmentMessage" =>
        Planner
          .execute[QueryRegistersByDepartmentMessagePlanner, List[Register]](
            requestJson
          )
      case "QueryRegistersByStudentMessage" =>
        Planner.execute[QueryRegistersByStudentMessagePlanner, List[Register]](
          requestJson
        )
      case "UpdateRegisterMessage" =>
        Planner.execute[UpdateRegisterMessagePlanner, String](requestJson)
      case _ =>
        IO.raiseError(
          new Exception(s"Unknown message type: $messageName")
        )

  def deleteRegister(
      registerId: String
  ): IO[Unit] =
    val sql =
      s"""
       |DELETE FROM ${thisService.schema}.${thisService.registerTable}
       |WHERE id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("Int", registerId)
      )

    for _ <- writeDB(sql, params) yield ()

  def queryRegistersByDepartment(
      departmentId: String
  ): IO[List[Register]] =
    val sql =
      s"""
       |SELECT *
       |FROM ${thisService.schema}.${thisService.registerTable}
       |WHERE department_id = ?;
      """.stripMargin

    val params: List[SqlParameter] =
      List(
        SqlParameter("Int", departmentId)
      )

    for
      rows <- readDBRows(sql, params)

      registers =
        rows.map: row =>
          Register(
            id = decodeField[Int](row, "id").toString,
            studentId = decodeField[String](row, "student_id"),
            departmentId = decodeField[Int](row, "department_id").toString
          )
    yield registers

  def queryRegistersByStudent(
      studentId: String
  ): IO[List[Register]] =
    val sql =
      s"""
       |SELECT *
       |FROM ${thisService.schema}.${thisService.registerTable}
       |WHERE student_id = ?;
      """.stripMargin

    val params: List[SqlParameter] =
      List(
        SqlParameter("String", studentId)
      )

    for
      rows <- readDBRows(sql, params)

      registers =
        rows.map: row =>
          Register(
            id = decodeField[Int](row, "id").toString,
            studentId = decodeField[String](row, "student_id"),
            departmentId = decodeField[Int](row, "department_id").toString
          )
    yield registers

  def updateRegister(
      register: Register
  ): IO[String] =
    val insertSql =
      s"""
       |INSERT INTO ${thisService.schema}.${thisService.registerTable} (student_id, department_id)
       |VALUES (?, ?)
       |RETURNING id;
      """.stripMargin

    val insertParams =
      List(
        SqlParameter("String", register.studentId),
        SqlParameter("Int", register.departmentId)
      )

    val updateSql =
      s"""
       |UPDATE ${thisService.schema}.${thisService.registerTable}
       |SET student_id = ?, department_id = ?
       |WHERE id = ?;
      """.stripMargin

    val updateParams =
      List(
        SqlParameter("String", register.studentId),
        SqlParameter("Int", register.departmentId),
        SqlParameter("Int", register.id)
      )

    if register.id.isEmpty then
      for id <- readDBInt(insertSql, insertParams)
      yield id.toString
    else
      for _ <- writeDB(updateSql, updateParams)
      yield register.id
