package APIs.UserService

import Common.API.API
import Global.UserService
import Objects.User

case class UpdateUserMessage(
    adminToken: String,
    user: User
) extends API[Unit](UserService, "UpdateUserMessage")
