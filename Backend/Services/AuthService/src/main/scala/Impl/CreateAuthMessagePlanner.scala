package Impl

import Utils.AuthManagementProcess.createAuth

import APIs.UserService.CheckUserMessage
import Common.API._
import Objects.Auth

import cats.effect.IO
import io.circe.generic.auto._

case class CreateAuthMessagePlanner(
    userId: String,
    password: String,
    planContext: PlanContext
) extends Planner[Auth]:
  override def plan: IO[Auth] =
    for
      userRole <- CheckUserMessage(userId, password).send
      auth <- createAuth(userRole)
    yield auth
