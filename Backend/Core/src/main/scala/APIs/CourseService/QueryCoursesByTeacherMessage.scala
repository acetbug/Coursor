package APIs.CourseService

import Common.API.API
import Global.ServiceCenter.CourseService
import Objects.Course

case class QueryCoursesByTeacherMessage(
    teacherId: String
) extends API[List[Course]](CourseService, "QueryCoursesByTeacherMessage")
