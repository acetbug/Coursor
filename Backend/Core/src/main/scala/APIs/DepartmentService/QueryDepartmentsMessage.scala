package APIs.DepartmentService

import Common.API.API
import Global.DepartmentService
import Objects.Department

case class QueryDepartmentsMessage(
    departmentIds: List[String]
) extends API[List[Department]](DepartmentService, "QueryDepartmentsMessage")
