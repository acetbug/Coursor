import APIs.AuthService.CheckAuthMessage
import APIs.TermService._
import Common.API.Planner
import Objects._

import cats.effect.IO
import io.circe.generic.auto._

case class DeleteSelectionRecordMessage(
    studentToken: String,
    selectionRecordId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(studentToken, UserRole.Student).send
      _ <- Utils.deleteSelectionRecord(selectionRecordId)
    yield ()

case class QuerySelectionRecordsMessage(
    studentId: String,
    termId: String
) extends Planner[List[SelectionRecord]]:
  def plan: IO[List[SelectionRecord]] =
    Utils.querySelectionRecords(studentId, termId)

case class UpdateSelectionRecordMessage(
    studentToken: String,
    selectionRecord: SelectionRecord
) extends Planner[String]:
  def plan: IO[String] =
    for
      _ <- CheckAuthMessage(studentToken, UserRole.Student).send
      selectionRecordId <- Utils.updateSelectionRecord(selectionRecord)
    yield selectionRecordId

case class ExecuteSelectionMessage(
    adminToken: String,
    termId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for 
      _ <- SwitchTermPhaseMessage(adminToken, termId).send
      
    yield ()
