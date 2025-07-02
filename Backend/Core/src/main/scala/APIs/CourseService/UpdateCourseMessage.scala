package APIs.CourseService

import Common.API.API
import Global.ServiceCenter.CourseService
import Objects.Course

case class UpdateCourseMessage(
    teacherToken: String,
    course: Course
) extends API[String](CourseService, "UpdateCourseMessage")
