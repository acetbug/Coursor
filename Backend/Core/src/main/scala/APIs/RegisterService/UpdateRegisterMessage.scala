package APIs.RegisterService

import Common.API.API
import Global.RegisterService
import Objects.Register

case class UpdateRegisterMessage(
    adminToken: String,
    register: Register
) extends API[String](RegisterService, "UpdateRegisterMessage")
