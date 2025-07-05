import APIs.AuthService.CheckAuthMessage
import Common.API.Planner
import Objects._

import cats.effect.IO
import io.circe.generic.auto._

case class DeleteStudyTraceMessagePlanner(
    adminToken: String,
    studyTraceId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      _ <- Utils.deleteStudyTrace(studyTraceId)
    yield ()

case class DeleteStudyRecordMessagePlanner(
    adminToken: String,
    studyRecordId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      _ <- Utils.deleteStudyRecord(studyRecordId)
    yield ()

case class QueryStudyTracesMessagePlanner(
    studentId: String
) extends Planner[List[StudyTrace]]:
  def plan: IO[List[StudyTrace]] =
    Utils.queryStudyTraces(studentId)

case class QueryStudyRecordsMessagePlanner(
    studentId: String
) extends Planner[List[StudyRecord]]:
  def plan: IO[List[StudyRecord]] =
    Utils.queryStudyRecords(studentId)

case class UpdateStudyTraceMessagePlanner(
    adminToken: String,
    studyTrace: StudyTrace
) extends Planner[String]:
  def plan: IO[String] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      studyTraceId <- Utils.updateStudyTrace(studyTrace)
    yield studyTraceId

case class UpdateStudyRecordMessagePlanner(
    adminToken: String,
    studyRecord: StudyRecord
) extends Planner[String]:
  def plan: IO[String] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      studyRecordId <- Utils.updateStudyRecord(studyRecord)
    yield studyRecordId
