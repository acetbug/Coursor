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
      |);
    """.stripMargin

  def handleRequest(
      messageName: String,
      requestJson: Json
  ): IO[Json] = messageName match
    case "CreateSelectionMessage" =>
      Planner.execute[CreateSelectionMessagePlanner, Unit](requestJson)
    case "QueryAllocatedStakePointsMessage" =>
      Planner.execute[QueryAllocatedStakePointsMessagePlanner, Int](requestJson)
    case "QuerySelectionsMessage" =>
      Planner.execute[QuerySelectionsMessagePlanner, List[Selection]](
        requestJson
      )
    case "UpdateStakeMessage" =>
      Planner.execute[UpdateStakeMessagePlanner, Unit](requestJson)
    case "UpdateSelectedCourseMessage" =>
      Planner.execute[UpdateSelectedCourseMessagePlanner, Unit](requestJson)
    case "DeleteSelectionMessage" =>
      Planner.execute[DeleteSelectionMessagePlanner, Unit](requestJson)
    case "ExecuteEnrollmentMessage" =>
      Planner.execute[ExecuteEnrollmentMessagePlanner, Unit](requestJson)
    case _ =>
      IO.raiseError(
        new Exception(s"Unknown message type: $messageName")
      )

  def checkTerm(
      termId: String
  ): IO[Unit] =
    for
      exists <- readDBBoolean(
        s"""
          |SELECT EXISTS (
          |    SELECT 1
          |    FROM ${RegisterService.schema}.${RegisterService.termTable}
          |    WHERE id = ? AND phase = ?
          |);
        """.stripMargin,
        List(
          SqlParameter("Int", termId),
          SqlParameter("String", Phase.Enrolling.toString)
        )
      )

      _ <-
        if exists then IO.unit
        else
          IO.raiseError(
            new Exception(s"Term $termId is not in enrolling phase")
          )
    yield ()

  def createSelection(
      studentId: String,
      termId: String,
      subjectId: String,
      courseId: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |INSERT INTO ${thisService.schema}.${thisService.enrollmentTable} (student_id, term_id, subject_id, course_id, points)
          |VALUES (?, ?, ?, ?, ?)
          |ON CONFLICT DO NOTHING;
        """.stripMargin,
        List(
          SqlParameter("String", studentId),
          SqlParameter("Int", termId),
          SqlParameter("Int", subjectId),
          SqlParameter("Int", courseId),
          SqlParameter("Int", "0")
        )
      )
    yield ()

  def queryAllocatedStakePoints(
      studentId: String,
      termId: String
  ): IO[Int] =
    readDBInt(
      s"""
        |SELECT COALESCE(SUM(points), 0)
        |FROM ${thisService.schema}.${thisService.enrollmentTable}
        |WHERE student_id = ? AND term_id = ?;
      """.stripMargin,
      List(
        SqlParameter("String", studentId),
        SqlParameter("Int", termId)
      )
    )

  def querySelections(
      studentId: String,
      termId: String
  ): IO[List[Selection]] =
    for rows <- readDBRows(
        s"""
          |SELECT id, subject_id, course_id, points
          |FROM ${thisService.schema}.${thisService.enrollmentTable}
          |WHERE student_id = ? AND term_id = ?;
        """.stripMargin,
        List(
          SqlParameter("String", studentId),
          SqlParameter("Int", termId)
        )
      )
    yield rows.map: row =>
      Selection(
        id = decodeField[Int](row, "id").toString,
        subjectId = decodeField[Int](row, "subject_id").toString,
        courseId = decodeField[Int](row, "course_id").toString,
        points = decodeField[Int](row, "points")
      )

  def updateStake(
      studentId: String,
      selectionId: String,
      points: Int
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |UPDATE ${thisService.schema}.${thisService.enrollmentTable}
          |SET points = ?
          |WHERE id = ? AND student_id = ?;
        """.stripMargin,
        List(
          SqlParameter("Int", points.toString),
          SqlParameter("Int", selectionId),
          SqlParameter("String", studentId)
        )
      )
    yield ()

  def updateSelectedCourse(
      studentId: String,
      selectionId: String,
      courseId: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |UPDATE ${thisService.schema}.${thisService.enrollmentTable}
          |SET course_id = ?
          |WHERE id = ? AND student_id = ?;
        """.stripMargin,
        List(
          SqlParameter("Int", courseId),
          SqlParameter("Int", selectionId),
          SqlParameter("String", studentId)
        )
      )
    yield ()

  def deleteSelection(
      studentId: String,
      selectionId: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |DELETE FROM ${thisService.schema}.${thisService.enrollmentTable}
          |WHERE id = ? AND student_id = ?;
        """.stripMargin,
        List(
          SqlParameter("Int", selectionId),
          SqlParameter("String", studentId)
        )
      )
    yield ()

  def executeEnrollment(
      termId: String
  ): IO[Unit] =
    for
      _ <- writeDB(
        s"""
          |UPDATE ${RegisterService.schema}.${RegisterService.termTable}
          |SET phase = ?
          |WHERE id = ?;
        """.stripMargin,
        List(
          SqlParameter("String", Phase.Confirmed.toString),
          SqlParameter("Int", termId)
        )
      )
      _ <- writeDB(
        s"""
          |DELETE FROM ${thisService.schema}.${thisService.enrollmentTable} AS E
          |USING (
          |    SELECT
          |        e.id,
          |        ROW_NUMBER() OVER (
          |            PARTITION BY e.course_id
          |            ORDER BY e.points DESC
          |        ) AS row_num,
          |        c.capacity AS keep_count
          |    FROM ${thisService.schema}.${thisService.enrollmentTable} e
          |    JOIN ${CurriculumService.schema}.${CurriculumService.courseTable} c ON e.course_id = c.id
          |    WHERE e.term_id = ?
          |) AS ranked
          |WHERE
          |    E.id = ranked.id
          |    AND ranked.row_num > ranked.keep_count;
        """.stripMargin,
        List(SqlParameter("Int", termId))
      )
    yield ()
