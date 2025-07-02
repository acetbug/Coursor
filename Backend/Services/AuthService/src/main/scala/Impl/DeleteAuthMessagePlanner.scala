package Impl

import Utils.AuthManagementProcess.deleteAuth

import Common.API._

import cats.effect.IO
import io.circe.generic.auto._

case class DeleteAuthMessagePlanner(
    token: String,
    planContext: PlanContext
) extends Planner[Unit]:
  override def plan: IO[Unit] = deleteAuth(token)
