package APIs.AuthService

import Common.API.API
import Global.ServiceCenter.AuthService

case class DeleteAuthMessage(
    token: String
) extends API[Unit](AuthService, "DeleteAuthMessage")
