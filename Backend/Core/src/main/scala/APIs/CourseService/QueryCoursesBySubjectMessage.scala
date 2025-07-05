package APIs.CourseService

import Common.API.API
import Global.CourseService
import Objects.Course

case class QueryCoursesBySubjectMessage(
    subjectId: String
) extends API[List[Course]](CourseService, "QueryCoursesBySubjectMessage")
