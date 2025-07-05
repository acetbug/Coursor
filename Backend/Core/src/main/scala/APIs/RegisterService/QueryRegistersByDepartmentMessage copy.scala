package APIs.RegisterService

import Common.API.API
import Global.RegisterService
import Objects.Register

case class QueryRegistersByDepartmentMessage(
    departmentId: String
) extends API[List[Register]](
      RegisterService,
      "QueryRegistersByDepartmentMessage"
    )
