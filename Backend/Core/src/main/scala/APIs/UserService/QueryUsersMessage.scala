package APIs.UserService

import Common.API.API
import Global.ServiceCenter.UserService
import Objects._

case class QueryUsersMessage(
    userRole: UserRole
) extends API[List[User]](UserService, "QueryUsersMessage")
