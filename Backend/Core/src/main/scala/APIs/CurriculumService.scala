package APIs

import Common.API.API
import Global.CurriculumService
import Objects._

import io.circe.generic.auto._

case class CreateSubjectMessage(
    adminToken: String,
    name: String,
    credits: Int
) extends API[Unit](CurriculumService, "CreateSubjectMessage")

case class CreateRecommendationMessage(
    adminToken: String,
    departmentId: String,
    subjectId: String,
    stage: Stage,
    priority: Int
) extends API[Unit](CurriculumService, "CreateRecommendationMessage")

case class CreateCourseMessage(
    teacherToken: String,
    subjectId: String,
    termId: String,
    location: String,
    schedule: String,
    capacity: Int
) extends API[Unit](CurriculumService, "CreateCourseMessage")

case class CreateCommentMessage(
    studentToken: String,
    courseId: String,
    content: String
) extends API[Unit](CurriculumService, "CreateCommentMessage")

case class QuerySubjectsMessage()
    extends API[List[Subject]](CurriculumService, "QuerySubjectsMessage")

case class QueryCurriculaMessage(departmentId: String)
    extends API[List[Curriculum]](
      CurriculumService,
      "QueryCurriculaMessage"
    )

case class QueryRecommendationsMessage(departmentId: String, stage: Stage)
    extends API[List[Recommendation]](
      CurriculumService,
      "QueryRecommendationsMessage"
    )

case class QueryTeachingsMessage(teacherId: String, termId: String)
    extends API[List[Teaching]](
      CurriculumService,
      "QueryTeachingsMessage"
    )

case class QueryCoursesMessage(subjectId: String, termId: String)
    extends API[List[Course]](CurriculumService, "QueryCoursesMessage")

case class QueryCommentsMessage(courseId: String)
    extends API[List[Comment]](CurriculumService, "QueryCommentsMessage")

case class QueryReviewsMessage(subjectId: String, teacherId: String)
    extends API[List[Review]](CurriculumService, "QueryReviewsMessage")

case class UpdateSubjectMessage(
    adminToken: String,
    subjectId: String,
    name: String,
    credits: Int
) extends API[Unit](CurriculumService, "UpdateSubjectMessage")

case class UpdateCourseMessage(
    teacherToken: String,
    courseId: String,
    location: String,
    schedule: String,
    capacity: Int
) extends API[Unit](CurriculumService, "UpdateCourseMessage")

case class DeleteSubjectMessage(adminToken: String, subjectId: String)
    extends API[Unit](CurriculumService, "DeleteSubjectMessage")

case class DeleteRecommendationMessage(
    adminToken: String,
    recommendationId: String
) extends API[Unit](CurriculumService, "DeleteRecommendationMessage")

case class DeleteCourseMessage(teacherToken: String, courseId: String)
    extends API[Unit](CurriculumService, "DeleteCourseMessage")

case class DeleteCommentMessage(studentToken: String, commentId: String)
    extends API[Unit](CurriculumService, "DeleteCommentMessage")
