package APIs.UserService

import Common.API.API
import Global.UserService
import Objects.UserRole

case class CheckUserMessage(
    userId: String,
    password: String
) extends API[UserRole](UserService, "CheckUserMessage")
