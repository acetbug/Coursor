package APIs

import Common.API.API
import Global.EnrollmentService
import Objects._

import io.circe.generic.auto._

case class CreateEnrollmentMessage(
    studentToken: String,
    termId: String,
    subjectId: String,
    courseId: String,
    points: Int
) extends API[Unit](EnrollmentService, "CreateEnrollmentMessage")

case class QueryStakePointsMessage(
    studentId: String,
    termId: String,
    departmentId: String,
    stage: Stage
) extends API[Int](EnrollmentService, "QueryStakePointsMessage")

case class QueryEnrollmentsMessage(
    studentId: String,
    termId: String
) extends API[List[EnrollmentGroup]](
      EnrollmentService,
      "QueryEnrollmentsMessage"
    )

case class QueryRecordsMessage(
    studentId: String,
    termId: String
) extends API[List[Enrollment]](EnrollmentService, "QueryRecordsMessage")

case class UpdateStakeMessage(
    studentToken: String,
    stakeId: String,
    points: Int
) extends API[Unit](EnrollmentService, "UpdateStakeMessage")

case class UpdateEnrollmentMessage(
    studentToken: String,
    stakeId: String,
    courseId: String
) extends API[Unit](EnrollmentService, "UpdateEnrollmentMessage")

case class DeleteStakeMessage(
    studentToken: String,
    stakeId: String
) extends API[Unit](EnrollmentService, "DeleteStakeMessage")

case class DeleteEnrollmentMessage(
    studentToken: String,
    courseId: String
) extends API[Unit](EnrollmentService, "DeleteEnrollmentMessage")
