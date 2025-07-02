package Impl

import Utils.AuthManagementProcess.checkAuth

import Common.API._
import Objects.{Auth, UserRole}

import cats.effect.IO
import io.circe.generic.auto._

case class CheckAuthMessagePlanner(
    token: String,
    userRole: UserRole,
    planContext: PlanContext
) extends Planner[Unit]:
  override def plan: IO[Unit] = checkAuth(token, userRole)
