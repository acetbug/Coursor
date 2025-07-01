package APIs.DepartmentService

import Common.API.API
import Global.ServiceCenter.DepartmentService

case class DeleteDepartmentMessage(
    adminToken: String,
    departmentId: String
) extends API[String](DepartmentService)
