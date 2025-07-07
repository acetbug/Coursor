import Common.DBAPI._
import Common.API.Planner
import Global._
import Objects._

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._

object Utils:
  val thisService = RegisterService
  val initSql =
    s"""
      |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.departmentTable} (
      |    id SERIAL PRIMARY KEY,
      |    name VARCHAR NOT NULL
      |);
      |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.termTable} (
      |    id SERIAL PRIMARY KEY,
      |    name VARCHAR NOT NULL,
      |    phase VARCHAR(10) NOT NULL
      |);
      |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.traceTable} (
      |    id SERIAL PRIMARY KEY,
      |    student_id VARCHAR NOT NULL
      |        REFERENCES ${UserService.schema}.${UserService.userTable}(id)
      |        ON DELETE CASCADE,
      |    term_id INT NOT NULL
      |        REFERENCES ${thisService.schema}.${thisService.termTable}(id)
      |        ON DELETE CASCADE,
      |    stage VARCHAR(12) NOT NULL,
      |    UNIQUE (student_id, term_id)
      |        ON CONFLICT DO NOTHING
      |);
      |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.registerTable} (
      |    id SERIAL PRIMARY KEY,
      |    student_id VARCHAR NOT NULL
      |        REFERENCES ${UserService.schema}.${UserService.userTable}(id)
      |        ON DELETE CASCADE,
      |    department_id INT NOT NULL
      |        REFERENCES ${DepartmentService.schema}.${DepartmentService.departmentTable}(id)
      |        ON DELETE CASCADE,
      |    UNIQUE (student_id)
      |        ON CONFLICT DO NOTHING
      |);
    """.stripMargin

  def handleRequest(
      messageName: String,
      requestJson: Json
  ): IO[Json] =
    messageName match
      case "CreateDepartmentMessage" =>
        Planner.execute[CreateDepartmentMessagePlanner, Unit](requestJson)
      case "CreateRegisterMessage" =>
        Planner.execute[CreateRegisterMessagePlanner, Unit](requestJson)
      case "CreateTermMessage" =>
        Planner.execute[CreateTermMessagePlanner, Unit](requestJson)
      case "CreateTraceMessage" =>
        Planner.execute[CreateTraceMessagePlanner, Unit](requestJson)
      case "QueryDepartmentsMessage" =>
        Planner.execute[QueryDepartmentsMessagePlanner, List[Department]](
          requestJson
        )
      case "QueryTermsMessage" =>
        Planner.execute[QueryTermsMessagePlanner, List[Term]](requestJson)
      case "QueryStudentMessage" =>
        Planner.execute[QueryStudentMessagePlanner, Student](requestJson)
      case "QueryStudentsMessage" =>
        Planner.execute[QueryStudentsMessagePlanner, List[User]](requestJson)
      case "UpdateDepartmentMessage" =>
        Planner.execute[UpdateDepartmentMessagePlanner, Unit](requestJson)
      case "UpdateTermMessage" =>
        Planner.execute[UpdateTermMessagePlanner, Unit](requestJson)
      case "DeleteDepartmentMessage" =>
        Planner.execute[DeleteDepartmentMessagePlanner, Unit](requestJson)
      case "DeleteTermMessage" =>
        Planner.execute[DeleteTermMessagePlanner, Unit](requestJson)
      case "DeleteRegisterMessage" =>
        Planner.execute[DeleteRegisterMessagePlanner, Unit](requestJson)
      case "DeleteTraceMessage" =>
        Planner.execute[DeleteTraceMessagePlanner, Unit](requestJson)
      case _ =>
        IO.raiseError(
          new Exception(s"Unknown message type: $messageName")
        )

  def createDepartment(
      name: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |INSERT INTO ${thisService.schema}.${thisService.departmentTable} (name)
          |VALUES (?);
        """.stripMargin,
        List(
          SqlParameter("String", name)
        )
      )
    yield ()

  def createRegister(
      studentId: String,
      departmentId: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |INSERT INTO ${thisService.schema}.${thisService.registerTable} (student_id, department_id)
          |VALUES (?, ?);
        """.stripMargin,
        List(
          SqlParameter("String", studentId),
          SqlParameter("Int", departmentId)
        )
      )
    yield ()

  def createTerm(
      name: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |INSERT INTO ${thisService.schema}.${thisService.termTable} (name, phase)
          |VALUES (?, ?);
        """.stripMargin,
        List(
          SqlParameter("String", name),
          SqlParameter("String", Phase.Enrolling.toString)
        )
      )
    yield ()

  def createTrace(
      studentId: String,
      termId: String,
      stage: Stage
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |INSERT INTO ${thisService.schema}.${thisService.traceTable} (student_id, term_id, stage)
          |VALUES (?, ?, ?);
        """.stripMargin,
        List(
          SqlParameter("String", studentId),
          SqlParameter("Int", termId),
          SqlParameter("String", stage.toString)
        )
      )
    yield ()

  def queryDepartments: IO[List[Department]] =
    for
      rows <- readDBRows(
        s"""
          |SELECT id, name
          |FROM ${thisService.schema}.${thisService.departmentTable};
        """.stripMargin,
        Nil
      )

      departments = rows.map: row =>
        Department(
          id = decodeField[Int](row, "id").toString,
          name = decodeField[String](row, "name")
        )
    yield departments

  def queryTerms: IO[List[Term]] =
    for
      rows <- readDBRows(
        s"""
          |SELECT id, name, phase
          |FROM ${thisService.schema}.${thisService.termTable};
        """.stripMargin,
        Nil
      )

      terms = rows.map: row =>
        Term(
          id = decodeField[Int](row, "id").toString,
          name = decodeField[String](row, "name"),
          phase = decodeField[Phase](row, "phase")
        )
    yield terms

  def queryStudent(studentId: String): IO[Student] =
    for
      jsonOpt <- readDBJsonOptional(
        s"""
          |SELECT d.id, d.name
          |FROM ${thisService.schema}.${thisService.registerTable} r
          |JOIN ${thisService.schema}.${thisService.departmentTable} d
          |ON r.department_id = d.id
          |WHERE r.student_id = ?;
        """.stripMargin,
        List(SqlParameter("String", studentId))
      )

      department = jsonOpt match
        case Some(json) =>
          Some(
            Department(
              id = decodeField[Int](json, "id").toString,
              name = decodeField[String](json, "name")
            )
          )
        case None => None

      rows <- readDBRows(
        s"""
          |SELECT term.id, term.name, term.phase, trace.stage
          |FROM ${thisService.schema}.${thisService.traceTable} trace
          |JOIN ${thisService.schema}.${thisService.termTable} term
          |ON trace.term_id = term.id
          |WHERE trace.student_id = ?;
        """.stripMargin,
        List(SqlParameter("String", studentId))
      )

      traces = rows.map: row =>
        Trace(
          term = Term(
            id = decodeField[Int](row, "id").toString,
            name = decodeField[String](row, "name"),
            phase = decodeField[Phase](row, "phase")
          ),
          stage = decodeField[Stage](row, "stage")
        )
    yield Student(
      department = department,
      traces = traces
    )

  def queryStudents(departmentId: String): IO[List[User]] =
    for
      rows <- readDBRows(
        s"""
          |SELECT u.id, u.name
          |FROM ${thisService.schema}.${thisService.registerTable} r
          |JOIN ${UserService.schema}.${UserService.userTable} u
          |ON r.student_id = u.id
          |WHERE r.department_id = ?;
        """.stripMargin,
        List(SqlParameter("Int", departmentId))
      )

      users = rows.map: row =>
        User(
          id = decodeField[Int](row, "id").toString,
          name = decodeField[String](row, "name")
        )
    yield users

  def updateDepartment(
      departmentId: String,
      name: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |UPDATE ${thisService.schema}.${thisService.departmentTable}
          |SET name = ?
          |WHERE id = ?;
        """.stripMargin,
        List(
          SqlParameter("String", name),
          SqlParameter("Int", departmentId)
        )
      )
    yield ()

  def updateTerm(
      termId: String,
      name: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |UPDATE ${thisService.schema}.${thisService.termTable}
          |SET name = ?
          |WHERE id = ?;
        """.stripMargin,
        List(
          SqlParameter("String", name),
          SqlParameter("Int", termId)
        )
      )
    yield ()

  def deleteDepartment(departmentId: String): IO[Unit] =
    for _ <- writeDB(
        s"""
          |DELETE FROM ${thisService.schema}.${thisService.departmentTable}
          |WHERE id = ?;
        """.stripMargin,
        List(
          SqlParameter("Int", departmentId)
        )
      )
    yield ()

  def deleteTerm(termId: String): IO[Unit] =
    for _ <- writeDB(
        s"""
          |DELETE FROM ${thisService.schema}.${thisService.termTable}
          |WHERE id = ?;
        """.stripMargin,
        List(
          SqlParameter("Int", termId)
        )
      )
    yield ()

  def deleteRegister(
      studentId: String,
      departmentId: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |DELETE FROM ${thisService.schema}.${thisService.registerTable}
          |WHERE student_id = ? AND department_id = ?;
        """.stripMargin,
        List(
          SqlParameter("String", studentId),
          SqlParameter("Int", departmentId)
        )
      )
    yield ()

  def deleteTrace(
      studentId: String,
      termId: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |DELETE FROM ${thisService.schema}.${thisService.traceTable}
          |WHERE student_id = ? AND term_id = ?;
        """.stripMargin,
        List(
          SqlParameter("String", studentId),
          SqlParameter("Int", termId)
        )
      )
    yield ()
