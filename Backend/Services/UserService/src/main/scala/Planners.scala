import APIs.AuthService.CheckAuthMessage
import Common.API.Planner
import Objects._

import cats.effect.IO
import io.circe.generic.auto._

case class CheckUserMessagePlanner(
    userId: String,
    password: String
) extends Planner[UserRole]:
  def plan: IO[UserRole] =
    Utils.checkUser(userId, password)

case class DeleteUserMessagePlanner(
    adminToken: String,
    userId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      _ <- Utils.deleteUser(userId)
    yield ()

case class QueryUsersMessagePlanner(
    userRole: UserRole
) extends Planner[List[User]]:
  def plan: IO[List[User]] =
    Utils.queryUsers(userRole)

case class UpdateUserMessagePlanner(
    adminToken: String,
    user: User
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      _ <- Utils.updateUser(user)
    yield ()
