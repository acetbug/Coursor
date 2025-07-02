package APIs.DepartmentService

import Common.API.API
import Global.ServiceCenter.DepartmentService
import Objects.Department

case class UpdateDepartmentMessage(
    adminToken: String,
    department: Department
) extends API[String](DepartmentService, "UpdateDepartmentMessage")
