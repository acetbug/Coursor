import APIs.AuthService.CheckAuthMessage
import Common.API.Planner
import Objects._

import cats.effect.IO
import io.circe.generic.auto._

case class DeleteTermMessagePlanner(
    adminToken: String,
    termId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      _ <- Utils.deleteTerm(termId)
    yield ()

case class QueryTermsMessagePlanner(
    termIds: List[String]
) extends Planner[List[Term]]:
  def plan: IO[List[Term]] =
    Utils.queryTerms(termIds)

case class UpdateTermMessagePlanner(
    adminToken: String,
    term: Term
) extends Planner[String]:
  def plan: IO[String] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      termId <- Utils.updateTerm(term)
    yield termId

case class SwitchTermPhaseMessagePlanner(
    adminToken: String,
    termId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      _ <- Utils.switchTermPhase(termId)
    yield ()
