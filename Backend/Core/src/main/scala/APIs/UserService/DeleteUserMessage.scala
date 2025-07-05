package APIs.UserService

import Common.API.API
import Global.UserService

case class DeleteUserMessage(
    adminToken: String,
    userId: String
) extends API[Unit](UserService, "DeleteUserMessage")
