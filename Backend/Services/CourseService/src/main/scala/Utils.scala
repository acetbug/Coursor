import Common.DBAPI._
import Common.API.Planner
import Global._
import Objects._

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._

object Utils:
  val thisService = CourseService

  def init: IO[Unit] =
    for
      _ <- initSchema(thisService.schema)
      _ <- writeDB(
        s"""
         |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.courseTable} (
         |    id SERIAL PRIMARY KEY,
         |    subject_id INT NOT NULL
         |        REFERENCES ${SubjectService.schema}.${SubjectService.subjectTable}(id)
         |        ON DELETE CASCADE,
         |    teacher_id VARCHAR NOT NULL
         |        REFERENCES ${UserService.schema}.${UserService.userTable}(id)
         |        ON DELETE CASCADE,
         |    term_id INT NOT NULL
         |        REFERENCES ${TermService.schema}.${TermService.termTable}(id)
         |        ON DELETE CASCADE,
         |    location TEXT NOT NULL,
         |    schedule TEXT NOT NULL,
         |    capacity INT NOT NULL
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
      case "DeleteCourseMessage" =>
        Planner.execute[DeleteCourseMessagePlanner, Unit](requestJson)
      case "QueryCoursesMessage" =>
        Planner.execute[QueryCoursesMessagePlanner, List[Course]](requestJson)
      case "QueryCoursesBySubjectMessage" =>
        Planner.execute[QueryCoursesBySubjectMessagePlanner, List[Course]](
          requestJson
        )
      case "QueryCoursesByTeacherMessage" =>
        Planner.execute[QueryCoursesByTeacherMessagePlanner, List[Course]](
          requestJson
        )
      case "UpdateCourseMessage" =>
        Planner.execute[UpdateCourseMessagePlanner, String](requestJson)
      case _ =>
        IO.raiseError(
          new Exception(s"Unknown message type: $messageName")
        )

  def deleteCourse(
      courseId: String
  ): IO[Unit] =
    val sql =
      s"""
       |DELETE FROM ${thisService.schema}.${thisService.courseTable}
       |WHERE id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("Int", courseId)
      )

    for _ <- writeDB(sql, params) yield ()

  def queryCourses(
      courseIds: List[String]
  ): IO[List[Course]] =
    val isAll = courseIds.isEmpty

    val sql =
      if isAll then s"""
         |SELECT *
         |FROM ${thisService.schema}.${thisService.courseTable};
        """.stripMargin
      else s"""
         |SELECT *
         |FROM ${thisService.schema}.${thisService.courseTable}
         |WHERE id IN (${courseIds.map(_ => "?").mkString(", ")});
        """.stripMargin

    val params =
      if isAll then Nil
      else courseIds.map(id => SqlParameter("Int", id))

    for
      rows <- readDBRows(sql, params)

      courses =
        rows.map: row =>
          Course(
            id = decodeField[Int](row, "id").toString,
            subjectId = decodeField[Int](row, "subject_id").toString,
            teacherId = decodeField[String](row, "teacher_id"),
            termId = decodeField[Int](row, "term_id").toString,
            location = decodeField[String](row, "location"),
            schedule = decodeField[String](row, "schedule"),
            capacity = decodeField[Int](row, "capacity")
          )
    yield courses

  def queryCoursesBySubject(
      subjectId: String
  ): IO[List[Course]] =
    val sql =
      s"""
       |SELECT *
       |FROM ${thisService.schema}.${thisService.courseTable}
       |WHERE subject_id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("Int", subjectId)
      )

    for
      rows <- readDBRows(sql, params)

      courses =
        rows.map: row =>
          Course(
            id = decodeField[Int](row, "id").toString,
            subjectId = decodeField[Int](row, "subject_id").toString,
            teacherId = decodeField[String](row, "teacher_id"),
            termId = decodeField[Int](row, "term_id").toString,
            location = decodeField[String](row, "location"),
            schedule = decodeField[String](row, "schedule"),
            capacity = decodeField[Int](row, "capacity")
          )
    yield courses

  def queryCoursesByTeacher(
      teacherId: String
  ): IO[List[Course]] =
    val sql =
      s"""
       |SELECT *
       |FROM ${thisService.schema}.${thisService.courseTable}
       |WHERE teacher_id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("String", teacherId)
      )

    for
      rows <- readDBRows(sql, params)

      courses =
        rows.map: row =>
          Course(
            id = decodeField[Int](row, "id").toString,
            subjectId = decodeField[Int](row, "subject_id").toString,
            teacherId = decodeField[String](row, "teacher_id"),
            termId = decodeField[Int](row, "term_id").toString,
            location = decodeField[String](row, "location"),
            schedule = decodeField[String](row, "schedule"),
            capacity = decodeField[Int](row, "capacity")
          )
    yield courses

  def updateCourse(
      course: Course
  ): IO[String] =
    val insertSql =
      s"""
       |INSERT INTO ${thisService.schema}.${thisService.courseTable} (subject_id, teacher_id, term_id, location, schedule, capacity)
       |VALUES (?, ?, ?, ?, ?, ?)
       |RETURNING id;
      """.stripMargin

    val insertParams =
      List(
        SqlParameter("Int", course.subjectId),
        SqlParameter("String", course.teacherId),
        SqlParameter("Int", course.termId),
        SqlParameter("String", course.location),
        SqlParameter("String", course.schedule),
        SqlParameter("Int", course.capacity.toString)
      )

    val updateSql =
      s"""
       |UPDATE ${thisService.schema}.${thisService.courseTable}
       |SET subject_id = ?, teacher_id = ?, term_id = ?, location = ?, schedule = ?, capacity = ?
       |WHERE id = ?;
      """.stripMargin

    val updateParams =
      List(
        SqlParameter("Int", course.subjectId),
        SqlParameter("String", course.teacherId),
        SqlParameter("Int", course.termId),
        SqlParameter("String", course.location),
        SqlParameter("String", course.schedule),
        SqlParameter("Int", course.capacity.toString),
        SqlParameter("Int", course.id)
      )

    if course.id.isEmpty then
      for id <- readDBInt(insertSql, insertParams)
      yield id.toString
    else
      for _ <- writeDB(updateSql, updateParams)
      yield course.id
