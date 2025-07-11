import APIs.CheckAuthMessage
import Common.API.Planner
import Objects._

import cats.effect.IO
import io.circe.generic.auto._

case class CreateSubjectMessagePlanner(
    adminToken: String,
    name: String,
    credits: Int
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, Role.Admin).send
      _ <- Utils.createSubject(name, credits)
    yield ()

case class CreateRecommendationMessagePlanner(
    adminToken: String,
    departmentId: String,
    subjectId: String,
    stage: Stage,
    priority: Int
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, Role.Admin).send
      _ <- Utils.createRecommendation(departmentId, subjectId, stage, priority)
    yield ()

case class CreateCourseMessagePlanner(
    teacherToken: String,
    subjectId: String,
    termId: String,
    location: String,
    schedule: String,
    capacity: Int
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      teacherId <- CheckAuthMessage(teacherToken, Role.Teacher).send
      _ <- Utils.createCourse(
        subjectId,
        teacherId,
        termId,
        location,
        schedule,
        capacity
      )
    yield ()

case class CreateCommentMessagePlanner(
    studentToken: String,
    courseId: String,
    content: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      studentId <- CheckAuthMessage(studentToken, Role.Student).send
      _ <- Utils.createComment(studentId, courseId, content)
    yield ()

case class QuerySubjectsMessagePlanner() extends Planner[List[Subject]]:
  def plan: IO[List[Subject]] =
    Utils.querySubjects

case class QueryCurriculaMessagePlanner(
    departmentId: String
) extends Planner[List[Curriculum]]:
  def plan: IO[List[Curriculum]] =
    Utils.queryCurricula(departmentId)

case class QueryRecommendationsMessagePlanner(
    departmentId: String,
    stage: Stage
) extends Planner[List[Recommendation]]:
  def plan: IO[List[Recommendation]] =
    Utils.queryRecommendations(departmentId, stage)

case class QueryTeachingsMessagePlanner(
    teacherId: String,
    termId: String
) extends Planner[List[Teaching]]:
  def plan: IO[List[Teaching]] =
    Utils.queryTeachings(teacherId, termId)

case class QueryCoursesMessagePlanner(
    subjectId: String,
    termId: String
) extends Planner[List[Course]]:
  def plan: IO[List[Course]] =
    Utils.queryCourses(subjectId, termId)

case class QueryCommentsMessagePlanner(
    courseId: String
) extends Planner[List[Comment]]:
  def plan: IO[List[Comment]] =
    Utils.queryComments(courseId)

case class QueryReviewsMessagePlanner(
    subjectId: String,
    teacherId: String
) extends Planner[List[Review]]:
  def plan: IO[List[Review]] =
    Utils.queryReviews(subjectId, teacherId)

case class UpdateSubjectMessagePlanner(
    adminToken: String,
    subjectId: String,
    name: String,
    credits: Int
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, Role.Admin).send
      _ <- Utils.updateSubject(subjectId, name, credits)
    yield ()

case class UpdateCourseMessagePlanner(
    teacherToken: String,
    courseId: String,
    location: String,
    schedule: String,
    capacity: Int
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      teacherId <- CheckAuthMessage(teacherToken, Role.Teacher).send
      _ <- Utils.updateCourse(teacherId, courseId, location, schedule, capacity)
    yield ()

case class DeleteSubjectMessagePlanner(
    adminToken: String,
    subjectId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, Role.Admin).send
      _ <- Utils.deleteSubject(subjectId)
    yield ()

case class DeleteRecommendationMessagePlanner(
    adminToken: String,
    recommendationId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, Role.Admin).send
      _ <- Utils.deleteRecommendation(recommendationId)
    yield ()

case class DeleteCourseMessagePlanner(
    teacherToken: String,
    courseId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      teacherId <- CheckAuthMessage(teacherToken, Role.Teacher).send
      _ <- Utils.deleteCourse(teacherId, courseId)
    yield ()

case class DeleteCommentMessagePlanner(
    studentToken: String,
    commentId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      studentId <- CheckAuthMessage(studentToken, Role.Student).send
      _ <- Utils.deleteComment(studentId, commentId)
    yield ()
