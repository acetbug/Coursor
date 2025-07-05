import APIs.AuthService.CheckAuthMessage
import Common.API.Planner
import Objects._

import cats.effect.IO
import io.circe.generic.auto._

case class DeleteCourseMessagePlanner(
    teacherToken: String,
    courseId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(teacherToken, UserRole.Teacher).send
      _ <- Utils.deleteCourse(courseId)
    yield ()

case class QueryCoursesMessagePlanner(
    courseIds: List[String]
) extends Planner[List[Course]]:
  def plan: IO[List[Course]] =
    Utils.queryCourses(courseIds)

case class QueryCoursesBySubjectMessagePlanner(
    subjectId: String
) extends Planner[List[Course]]:
  def plan: IO[List[Course]] =
    Utils.queryCoursesBySubject(subjectId)

case class QueryCoursesByTeacherMessagePlanner(
    teacherId: String
) extends Planner[List[Course]]:
  def plan: IO[List[Course]] =
    Utils.queryCoursesByTeacher(teacherId)

case class UpdateCourseMessagePlanner(
    teacherToken: String,
    course: Course
) extends Planner[String]:
  def plan: IO[String] =
    for
      _ <- CheckAuthMessage(teacherToken, UserRole.Teacher).send
      courseId <- Utils.updateCourse(course)
    yield courseId
