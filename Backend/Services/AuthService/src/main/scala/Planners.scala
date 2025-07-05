import APIs.UserService.CheckUserMessage
import Common.API.Planner
import Objects._

import cats.effect.IO
import io.circe.generic.auto._

case class CheckAuthMessagePlanner(
    token: String,
    userRole: UserRole
) extends Planner[Unit]:
  def plan: IO[Unit] =
    Utils.checkAuth(token, userRole)

case class CreateAuthMessagePlanner(
    userId: String,
    password: String
) extends Planner[Auth]:
  def plan: IO[Auth] =
    for
      userRole <- CheckUserMessage(userId, password).send
      auth <- Utils.createAuth(userRole)
    yield auth

case class DeleteAuthMessagePlanner(
    token: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    Utils.deleteAuth(token)
