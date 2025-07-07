import Common.API.Planner
import Objects._

import cats.effect.IO
import io.circe.generic.auto._

case class LoginMessagePlanner(
    userId: String,
    password: String
) extends Planner[UserAuth]:
  def plan: IO[UserAuth] =
    Utils.login(userId, password)

case class LogoutMessagePlanner(token: String) extends Planner[Unit]:
  def plan: IO[Unit] =
    Utils.logout(token)

case class CheckAuthMessagePlanner(
    token: String,
    role: Role
) extends Planner[String]:
  def plan: IO[String] =
    Utils.checkAuth(token, role)

case class CheckUserRoleMessagePlanner(
    userId: String,
    role: Role
) extends Planner[Unit]:
  def plan: IO[Unit] =
    Utils.checkUserRole(userId, role)

case class CreateUserMessagePlanner(
    adminToken: String,
    userId: String,
    password: String,
    name: String,
    role: Role
) extends Planner[Unit]:
  def plan: IO[Unit] =
    Utils.checkAuth(adminToken, Role.Admin) >>
      Utils.createUser(userId, password, name, role)

case class QueryUsersMessagePlanner(role: Role) extends Planner[List[User]]:
  def plan: IO[List[User]] =
    Utils.queryUsers(role)

case class UpdateUserPasswordMessagePlanner(
    adminToken: String,
    userId: String,
    password: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    Utils.checkAuth(adminToken, Role.Admin) >>
      Utils.updateUserPassword(userId, password)

case class UpdateUserNameMessagePlanner(
    adminToken: String,
    userId: String,
    name: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    Utils.checkAuth(adminToken, Role.Admin) >>
      Utils.updateUserName(userId, name)

case class DeleteUserMessagePlanner(
    adminToken: String,
    userId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    Utils.checkAuth(adminToken, Role.Admin) >>
      Utils.deleteUser(userId)
