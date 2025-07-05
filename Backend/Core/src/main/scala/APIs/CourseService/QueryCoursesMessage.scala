package APIs.CourseService

import Common.API.API
import Global.CourseService
import Objects.Course

case class QueryCoursesMessage(
    courseIds: List[String]
) extends API[List[Course]](CourseService, "QueryCoursesMessage")
