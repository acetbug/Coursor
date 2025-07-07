package APIs

import Common.API.API
import Global.RegisterService
import Objects._

import io.circe.generic.auto._

case class CreateDepartmentMessage(
    adminToken: String,
    name: String
) extends API[Unit](RegisterService, "CreateDepartmentMessage")

case class CreateRegisterMessage(
    adminToken: String,
    studentId: String,
    departmentId: String
) extends API[Unit](RegisterService, "CreateRegisterMessage")

case class CreateTermMessage(
    adminToken: String,
    name: String
) extends API[Unit](RegisterService, "CreateTermMessage")

case class CreateTraceMessage(
    adminToken: String,
    studentId: String,
    termId: String,
    stage: Stage
) extends API[Unit](RegisterService, "CreateTraceMessage")

case class QueryDepartmentsMessage()
    extends API[List[Department]](RegisterService, "QueryDepartmentsMessage")

case class QueryTermsMessage()
    extends API[List[Term]](RegisterService, "QueryTermsMessage")

case class QueryStudentMessage(
    studentId: String
) extends API[Student](RegisterService, "QueryStudentMessage")

case class QueryStudentsMessage(
    departmentId: String
) extends API[List[User]](RegisterService, "QueryStudentsMessage")

case class UpdateDepartmentMessage(
    adminToken: String,
    departmentId: String,
    name: String
) extends API[Unit](RegisterService, "UpdateDepartmentMessage")

case class UpdateTermMessage(
    adminToken: String,
    termId: String,
    name: String
) extends API[Unit](RegisterService, "UpdateTermMessage")

case class DeleteDepartmentMessage(
    adminToken: String,
    departmentId: String
) extends API[Unit](RegisterService, "DeleteDepartmentMessage")

case class DeleteTermMessage(
    adminToken: String,
    termId: String
) extends API[Unit](RegisterService, "DeleteTermMessage")

case class DeleteRegisterMessage(
    adminToken: String,
    studentId: String,
    departmentId: String
) extends API[Unit](RegisterService, "DeleteRegisterMessage")

case class DeleteTraceMessage(
    adminToken: String,
    studentId: String,
    termId: String
) extends API[Unit](RegisterService, "DeleteTraceMessage")
