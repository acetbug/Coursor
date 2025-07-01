package APIs.UserService

import Common.API.API
import Global.ServiceCenter.UserService
import Objects.User

case class UpdateUserMessage(
    adminToken: String,
    user: User
) extends API[String](UserService)
