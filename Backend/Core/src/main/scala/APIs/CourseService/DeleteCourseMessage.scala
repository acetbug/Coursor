package APIs.CourseService

import Common.API.API
import Global.CourseService

case class DeleteCourseMessage(
    teacherToken: String,
    courseId: String
) extends API[Unit](CourseService, "DeleteCourseMessage")
