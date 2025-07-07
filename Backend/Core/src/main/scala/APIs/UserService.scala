package APIs

import Common.API.API
import Global.UserService
import Objects._

import io.circe.generic.auto._

case class LoginMessage(userId: String, password: String)
    extends API[UserAuth](UserService, "LoginMessage")

case class LogoutMessage(token: String)
    extends API[Unit](UserService, "LogoutMessage")

case class CheckAuthMessage(token: String, role: Role)
    extends API[String](UserService, "CheckAuthMessage")

case class CheckUserRoleMessage(
    userId: String,
    role: Role
) extends API[Unit](UserService, "CheckUserRoleMessage")

case class CreateUserMessage(
    adminToken: String,
    userId: String,
    password: String,
    name: String,
    role: Role
) extends API[Unit](UserService, "CreateUserMessage")

case class QueryUsersMessage(role: Role)
    extends API[List[User]](UserService, "QueryUsersMessage")

case class UpdateUserPasswordMessage(
    adminToken: String,
    userId: String,
    password: String
) extends API[Unit](UserService, "UpdateUserPasswordMessage")

case class UpdateUserNameMessage(
    adminToken: String,
    userId: String,
    name: String
) extends API[Unit](UserService, "UpdateUserNameMessage")

case class DeleteUserMessage(adminToken: String, userId: String)
    extends API[Unit](UserService, "DeleteUserMessage")
