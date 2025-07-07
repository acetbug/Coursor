import Common.DBAPI._
import Common.API.Planner
import Global._
import Objects._

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._

object Utils:
  val thisService = EnrollmentService
  val initSql =
    s"""
      |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.enrollmentTable} (
      |    id SERIAL PRIMARY KEY,
      |    student_id VARCHAR NOT NULL
      |        REFERENCES ${UserService.schema}.${UserService.userTable}(id)
      |        ON DELETE CASCADE,
      |    term_id INT NOT NULL
      |        REFERENCES ${RegisterService.schema}.${RegisterService.termTable}(id)
      |        ON DELETE CASCADE,
      |    subject_id INT NOT NULL
      |        REFERENCES ${CurriculumService.schema}.${CurriculumService.subjectTable}(id)
      |        ON DELETE CASCADE,
      |    course_id INT NOT NULL
      |        REFERENCES ${CurriculumService.schema}.${CurriculumService.courseTable}(id)
      |        ON DELETE CASCADE,
      |    points INT NOT NULL,
      |    UNIQUE (student_id, term_id, subject_id)
      |        ON CONFLICT DO NOTHING
      |);
    """.stripMargin

  def handleRequest(
      messageName: String,
      requestJson: Json
  ): IO[Json] = messageName match

    case _ =>
      IO.raiseError(
        new Exception(s"Unknown message type: $messageName")
      )

  def createEnrollment(
      studentId: String,
      termId: String,
      subjectId: String,
      courseId: String,
      points: Int
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |INSERT INTO ${thisService.schema}.${thisService.enrollmentTable} (student_id, term_id, subject_id, course_id, points)
          |VALUES (?, ?, ?, ?, ?);
        """.stripMargin,
        List(
          SqlParameter("String", studentId),
          SqlParameter("String", termId),
          SqlParameter("String", subjectId),
          SqlParameter("String", courseId),
          SqlParameter("Int", points.toString)
        )
      )
    yield ()

  def queryStakePoints(
      studentId: String,
      termId: String,
      departmentId: String,
      stage: Stage
  ): IO[Int] =
    readDBInt(
      s"""
        |SELECT COALESCE(SUM(r.priority), 0) * 30
        |FROM ${thisService.schema}.${thisService.enrollmentTable} e
        |JOIN ${CurriculumService.schema}.${CurriculumService.recommendationTable} r
        |ON e.subject_id = r.subject_id
        |WHERE e.student_id = ? AND e.term_id = ? AND r.department_id = ? AND r.stage = ?;
      """.stripMargin,
      List(
        SqlParameter("String", studentId),
        SqlParameter("Int", departmentId),
        SqlParameter("Int", termId),
        SqlParameter("String", stage.toString)
      )
    )
