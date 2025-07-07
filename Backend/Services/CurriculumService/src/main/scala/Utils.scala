import Common.DBAPI._
import Common.API.Planner
import Global._
import Objects._

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._

object Utils:
  val thisService = CurriculumService
  val initSql =
    s"""
      |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.subjectTable} (
      |    id SERIAL PRIMARY KEY,
      |    name VARCHAR NOT NULL,
      |    credits INT NOT NULL
      |);
      |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.recommendationTable} (
      |    id SERIAL PRIMARY KEY,
      |    department_id INT NOT NULL
      |        REFERENCES ${RegisterService.schema}.${RegisterService.departmentTable}(id)
      |        ON DELETE CASCADE,
      |    subject_id INT NOT NULL
      |        REFERENCES ${thisService.schema}.${thisService.subjectTable}(id)
      |        ON DELETE CASCADE,
      |    stage VARCHAR(12) NOT NULL,
      |    priority INT NOT NULL,
      |    UNIQUE (department_id, subject_id)
      |        ON CONFLICT DO NOTHING
      |);
      |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.courseTable} (
      |    id SERIAL PRIMARY KEY,
      |    subject_id INT NOT NULL
      |        REFERENCES ${thisService.schema}.${thisService.subjectTable}(id)
      |        ON DELETE CASCADE,
      |    teacher_id VARCHAR NOT NULL
      |        REFERENCES ${UserService.schema}.${UserService.userTable}(id)
      |        ON DELETE CASCADE,
      |    term_id INT NOT NULL
      |        REFERENCES ${RegisterService.schema}.${RegisterService.termTable}(id)
      |        ON DELETE CASCADE,
      |    location TEXT NOT NULL,
      |    schedule TEXT NOT NULL,
      |    capacity INT NOT NULL
      |);
      |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.commentTable} (
      |    id SERIAL PRIMARY KEY,
      |    course_id INT NOT NULL
      |        REFERENCES ${thisService.schema}.${thisService.courseTable}(id)
      |        ON DELETE CASCADE,
      |    student_id VARCHAR NOT NULL
      |        REFERENCES ${UserService.schema}.${UserService.userTable}(id)
      |        ON DELETE CASCADE,
      |    content TEXT NOT NULL
      |);
    """.stripMargin

  def handleRequest(
      messageName: String,
      requestJson: Json
  ): IO[Json] = messageName match
    case "CreateSubjectMessage" =>
      Planner.execute[CreateSubjectMessagePlanner, Unit](requestJson)
    case "CreateRecommendationMessage" =>
      Planner.execute[CreateRecommendationMessagePlanner, Unit](requestJson)
    case "CreateCourseMessage" =>
      Planner.execute[CreateCourseMessagePlanner, Unit](requestJson)
    case "CreateCommentMessage" =>
      Planner.execute[CreateCommentMessagePlanner, Unit](requestJson)
    case "QuerySubjectsMessage" =>
      Planner.execute[QuerySubjectsMessagePlanner, List[Subject]](requestJson)
    case "QueryCurriculaMessage" =>
      Planner.execute[QueryCurriculaMessagePlanner, List[Curriculum]](
        requestJson
      )
    case "QueryTeachingsMessage" =>
      Planner.execute[QueryTeachingsMessagePlanner, List[Teaching]](requestJson)
    case "QueryCoursesMessage" =>
      Planner.execute[QueryCoursesMessagePlanner, List[Course]](requestJson)
    case "QueryCommentsMessage" =>
      Planner.execute[QueryCommentsMessagePlanner, List[Comment]](requestJson)
    case "QueryReviewsMessage" =>
      Planner.execute[QueryReviewsMessagePlanner, List[Review]](requestJson)
    case "UpdateSubjectMessage" =>
      Planner.execute[UpdateSubjectMessagePlanner, Unit](requestJson)
    case "UpdateCourseMessage" =>
      Planner.execute[UpdateCourseMessagePlanner, Unit](requestJson)
    case "DeleteSubjectMessage" =>
      Planner.execute[DeleteSubjectMessagePlanner, Unit](requestJson)
    case "DeleteRecommendationMessage" =>
      Planner.execute[DeleteRecommendationMessagePlanner, Unit](requestJson)
    case "DeleteCourseMessage" =>
      Planner.execute[DeleteCourseMessagePlanner, Unit](requestJson)
    case "DeleteCommentMessage" =>
      Planner.execute[DeleteCommentMessagePlanner, Unit](requestJson)
    case _ =>
      IO.raiseError(
        new Exception(s"Unknown message type: $messageName")
      )

  def createSubject(
      name: String,
      credits: Int
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |INSERT INTO ${thisService.schema}.${thisService.subjectTable} (name, credits)
          |VALUES (?, ?);
        """.stripMargin,
        List(
          SqlParameter("String", name),
          SqlParameter("Int", credits.toString)
        )
      )
    yield ()

  def createRecommendation(
      departmentId: String,
      subjectId: String,
      stage: Stage,
      priority: Int
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |INSERT INTO ${thisService.schema}.${thisService.recommendationTable} (department_id, subject_id, stage, priority)
          |VALUES (?, ?, ?, ?);
        """.stripMargin,
        List(
          SqlParameter("Int", departmentId),
          SqlParameter("Int", subjectId),
          SqlParameter("String", stage.toString),
          SqlParameter("Int", priority.toString)
        )
      )
    yield ()

  def createCourse(
      subjectId: String,
      teacherId: String,
      termId: String,
      location: String,
      schedule: String,
      capacity: Int
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |INSERT INTO ${thisService.schema}.${thisService.courseTable} (subject_id, teacher_id, term_id, location, schedule, capacity)
          |VALUES (?, ?, ?, ?, ?, ?);
        """.stripMargin,
        List(
          SqlParameter("Int", subjectId),
          SqlParameter("String", teacherId),
          SqlParameter("Int", termId),
          SqlParameter("String", location),
          SqlParameter("String", schedule),
          SqlParameter("Int", capacity.toString)
        )
      )
    yield ()

  def createComment(
      studentId: String,
      courseId: String,
      content: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |INSERT INTO ${thisService.schema}.${thisService.commentTable} (student_id, course_id, content)
          |VALUES (?, ?, ?);
        """.stripMargin,
        List(
          SqlParameter("String", studentId),
          SqlParameter("Int", courseId),
          SqlParameter("String", content)
        )
      )
    yield ()

  def querySubjects: IO[List[Subject]] =
    for rows <- readDBRows(
        s"""
          |SELECT id, name, credits
          |FROM ${thisService.schema}.${thisService.subjectTable};
        """.stripMargin,
        List.empty
      )
    yield rows.map: row =>
      Subject(
        id = decodeField[Int](row, "id").toString,
        name = decodeField[String](row, "name"),
        credits = decodeField[Int](row, "credits")
      )

  def queryCurricula(
      departmentId: String
  ): IO[List[Curriculum]] =
    for rows <- readDBRows(
        s"""
          |SELECT r.id AS recommendation_id, r.subject_id, r.stage, r.priority, s.name, s.credits
          |FROM ${thisService.schema}.${thisService.recommendationTable} r
          |JOIN ${thisService.schema}.${thisService.subjectTable} s
          |ON r.subject_id = s.id
          |WHERE r.department_id = ?;
        """.stripMargin,
        List(SqlParameter("Int", departmentId))
      )
    yield rows
      .groupBy(decodeField[Stage](_, "stage"))
      .map: (stage, rows) =>
        Curriculum(
          stage = stage,
          recommendations = rows.map: row =>
            Recommendation(
              id = decodeField[Int](row, "recommendation_id").toString,
              subject = Subject(
                id = decodeField[Int](row, "subject_id").toString,
                name = decodeField[String](row, "name"),
                credits = decodeField[Int](row, "credits")
              ),
              priority = decodeField[Int](row, "priority")
            )
        )
      .toList

  def queryTeachings(teacherId: String, termId: String): IO[List[Teaching]] =
    for rows <- readDBRows(
        s"""
            |SELECT c.id AS course_id, c.subject_id, s.name AS subject_name, s.credits, c.term_id, t.name AS term_name, t.phase, c.location, c.schedule, c.capacity
            |FROM ${thisService.schema}.${thisService.courseTable} c
            |JOIN ${thisService.schema}.${thisService.subjectTable} s
            |ON c.subject_id = s.id
            |JOIN ${RegisterService.schema}.${RegisterService.termTable} t
            |ON c.term_id = t.id
            |WHERE c.teacher_id = ? AND c.term_id = ?;
          """.stripMargin,
        List(
          SqlParameter("String", teacherId),
          SqlParameter("Int", termId)
        )
      )
    yield rows
      .groupBy(decodeField[Int](_, "subject_id"))
      .map: (subjectId, rows) =>
        Teaching(
          subject = Subject(
            id = subjectId.toString,
            name = decodeField[String](rows.head, "subject_name"),
            credits = decodeField[Int](rows.head, "credits")
          ),
          lectures = rows.map: row =>
            Lecture(
              id = decodeField[Int](row, "course_id").toString,
              term = Term(
                id = decodeField[Int](row, "term_id").toString,
                name = decodeField[String](row, "term_name"),
                phase = decodeField[Phase](row, "phase")
              ),
              location = decodeField[String](row, "location"),
              schedule = decodeField[String](row, "schedule"),
              capacity = decodeField[Int](row, "capacity")
            )
        )
      .toList

  def queryCourses(
      subjectId: String,
      termId: String
  ): IO[List[Course]] =
    for rows <- readDBRows(
        s"""
          |SELECT c.id, c.teacher_id, u.name AS teacher_name, c.location, c.schedule, c.capacity
          |FROM ${thisService.schema}.${thisService.courseTable} c
          |JOIN ${UserService.schema}.${UserService.userTable} u
          |ON c.teacher_id = u.id
          |WHERE c.subject_id = ? AND c.term_id = ?;
        """.stripMargin,
        List(
          SqlParameter("Int", subjectId),
          SqlParameter("Int", termId)
        )
      )
    yield rows.map: row =>
      Course(
        id = decodeField[Int](row, "id").toString,
        teacher = User(
          id = decodeField[String](row, "teacher_id"),
          name = decodeField[String](row, "teacher_name")
        ),
        location = decodeField[String](row, "location"),
        schedule = decodeField[String](row, "schedule"),
        capacity = decodeField[Int](row, "capacity")
      )

  def queryComments(
      courseId: String
  ): IO[List[Comment]] =
    for rows <- readDBRows(
        s"""
          |SELECT id, content
          |FROM ${thisService.schema}.${thisService.commentTable}
          |WHERE course_id = ?;
        """.stripMargin,
        List(SqlParameter("Int", courseId))
      )
    yield rows.map: row =>
      Comment(
        id = decodeField[Int](row, "id").toString,
        content = decodeField[String](row, "content")
      )

  def queryReviews(
      subjectId: String,
      teacherId: String
  ): IO[List[Review]] =
    for rows <- readDBRows(
        s"""
          |SELECT term.id AS term_id, term.name, term.phase, comment.id AS comment_id, comment.content
          |FROM ${thisService.schema}.${thisService.courseTable} course
          |JOIN ${thisService.schema}.${thisService.commentTable} comment
          |ON course.id = comment.course_id
          |JOIN ${RegisterService.schema}.${RegisterService.termTable} term
          |ON course.term_id = term.id
          |WHERE course.subject_id = ? AND course.teacher_id = ?;
        """.stripMargin,
        List(
          SqlParameter("Int", subjectId),
          SqlParameter("String", teacherId)
        )
      )
    yield rows
      .groupBy(decodeField[Int](_, "term_id"))
      .map: (termId, rows) =>
        Review(
          term = Term(
            id = termId.toString,
            name = decodeField[String](rows.head, "name"),
            phase = decodeField[Phase](rows.head, "phase")
          ),
          comments = rows.map: row =>
            Comment(
              id = decodeField[Int](row, "comment_id").toString,
              content = decodeField[String](row, "content")
            )
        )
      .toList

  def updateSubject(
      subjectId: String,
      name: String,
      credits: Int
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |UPDATE ${thisService.schema}.${thisService.subjectTable}
          |SET name = ?, credits = ?
          |WHERE id = ?;
        """.stripMargin,
        List(
          SqlParameter("String", name),
          SqlParameter("Int", credits.toString),
          SqlParameter("Int", subjectId)
        )
      )
    yield ()

  def updateCourse(
      teacherId: String,
      courseId: String,
      location: String,
      schedule: String,
      capacity: Int
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |UPDATE ${thisService.schema}.${thisService.courseTable}
          |SET location = ?, schedule = ?, capacity = ?
          |WHERE id = ? AND teacher_id = ?;
        """.stripMargin,
        List(
          SqlParameter("String", location),
          SqlParameter("String", schedule),
          SqlParameter("Int", capacity.toString),
          SqlParameter("Int", courseId),
          SqlParameter("String", teacherId)
        )
      )
    yield ()

  def deleteSubject(
      subjectId: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |DELETE FROM ${thisService.schema}.${thisService.subjectTable}
          |WHERE id = ?;
        """.stripMargin,
        List(SqlParameter("Int", subjectId))
      )
    yield ()

  def deleteRecommendation(
      recommendationId: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |DELETE FROM ${thisService.schema}.${thisService.recommendationTable}
          |WHERE id = ?;
        """.stripMargin,
        List(SqlParameter("Int", recommendationId))
      )
    yield ()

  def deleteCourse(
      teacherId: String,
      courseId: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |DELETE FROM ${thisService.schema}.${thisService.courseTable}
          |WHERE id = ? AND teacher_id = ?;
        """.stripMargin,
        List(
          SqlParameter("Int", courseId),
          SqlParameter("String", teacherId)
        )
      )
    yield ()

  def deleteComment(
      studentId: String,
      commentId: String
  ): IO[Unit] =
    for _ <- writeDB(
        s"""
          |DELETE FROM ${thisService.schema}.${thisService.commentTable}
          |WHERE id = ? AND student_id = ?;
        """.stripMargin,
        List(
          SqlParameter("Int", commentId),
          SqlParameter("String", studentId)
        )
      )
    yield ()
