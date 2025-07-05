package APIs.RegisterService

import Common.API.API
import Global.RegisterService

case class DeleteRegisterMessage(
    adminToken: String,
    registerId: String
) extends API[Unit](RegisterService, "DeleteRegisterMessage")
