package APIs.RegisterService

import Common.API.API
import Global.RegisterService
import Objects.Register

case class QueryRegistersByStudentMessage(
    studentId: String
) extends API[List[Register]](RegisterService, "QueryRegistersByStudentMessage")
