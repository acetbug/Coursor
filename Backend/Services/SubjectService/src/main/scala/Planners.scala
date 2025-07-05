import APIs.AuthService.CheckAuthMessage
import Common.API.Planner
import Objects._

import cats.effect.IO
import io.circe.generic.auto._

case class DeleteSubjectMessagePlanner(
    adminToken: String,
    subjectId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      _ <- Utils.deleteSubject(subjectId)
    yield ()

case class QuerySubjectsMessagePlanner(
    subjectIds: List[String]
) extends Planner[List[Subject]]:
  def plan: IO[List[Subject]] =
    Utils.querySubjects(subjectIds)

case class UpdateSubjectMessagePlanner(
    adminToken: String,
    subject: Subject
) extends Planner[String]:
  def plan: IO[String] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      subjectId <- Utils.updateSubject(subject)
    yield subjectId
