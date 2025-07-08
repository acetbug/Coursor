package APIs

import Common.API.API
import Global.EnrollmentService
import Objects._

import io.circe.generic.auto._

case class CreateSelectionMessage(
    studentToken: String,
    termId: String,
    subjectId: String,
    courseId: String
) extends API[Unit](EnrollmentService, "CreateSelectionMessage")

case class QueryAllocatedStakePointsMessage(
    studentId: String,
    termId: String
) extends API[Int](EnrollmentService, "QueryAllocatedStakePointsMessage")

case class QuerySelectionsMessage(
    studentId: String,
    termId: String
) extends API[List[Selection]](
      EnrollmentService,
      "QuerySelectionsMessage"
    )

case class UpdateStakeMessage(
    studentToken: String,
    termId: String,
    selectionId: String,
    points: Int
) extends API[Unit](EnrollmentService, "UpdateStakeMessage")

case class UpdateSelectedCourseMessage(
    studentToken: String,
    termId: String,
    selectionId: String,
    courseId: String
) extends API[Unit](EnrollmentService, "UpdateSelectedCourseMessage")

case class DeleteSelectionMessage(
    studentToken: String,
    termId: String,
    selectionId: String
) extends API[Unit](EnrollmentService, "DeleteSelectionMessage")

case class ExecuteEnrollmentMessage(
    adminToken: String,
    termId: String,
) extends API[Unit](EnrollmentService, "ExecuteEnrollmentMessage")
