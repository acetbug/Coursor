package APIs.UserService

import Common.API.API
import Global.ServiceCenter.UserService

case class DeleteUserMessage(
    adminToken: String,
    userId: String
) extends API[String](UserService)
