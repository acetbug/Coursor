package APIs.DepartmentService

import Common.API.API
import Global.DepartmentService

case class DeleteDepartmentMessage(
    adminToken: String,
    departmentId: String
) extends API[Unit](DepartmentService, "DeleteDepartmentMessage")
