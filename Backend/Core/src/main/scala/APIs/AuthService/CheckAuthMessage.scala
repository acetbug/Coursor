package APIs.AuthService

import Common.API.API
import Global.AuthService
import Objects.UserRole

case class CheckAuthMessage(
    token: String,
    userRole: UserRole
) extends API[Unit](AuthService, "CheckAuthMessage")
