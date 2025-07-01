package APIs.DepartmentService

import Common.API.API
import Global.ServiceCenter.DepartmentService
import Objects.Department

case class QueryDepartmentMessage(
    departmentId: String
) extends API[Department](DepartmentService)
