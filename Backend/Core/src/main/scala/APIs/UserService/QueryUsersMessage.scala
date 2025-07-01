package APIs.UserService

import Common.API.API
import Global.ServiceCenter.UserService
import Objects.User

case class QueryUsersMessage(
    adminToken: String
) extends API[List[User]](UserService)
