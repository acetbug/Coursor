import APIs.CheckAuthMessage
import Common.API.Planner
import Objects._

import cats.effect.IO
import io.circe.generic.auto._

case class CreateSelectionMessagePlanner(
    studentToken: String,
    termId: String,
    subjectId: String,
    courseId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      studentId <- CheckAuthMessage(studentToken, Role.Student).send
      _ <- Utils.checkTerm(termId)
      _ <- Utils.createSelection(
        studentId,
        termId,
        subjectId,
        courseId
      )
    yield ()

case class QueryAllocatedStakePointsMessagePlanner(
    studentId: String,
    termId: String
) extends Planner[Int]:
  def plan: IO[Int] =
    Utils.queryAllocatedStakePoints(studentId, termId)

case class QuerySelectionsMessagePlanner(
    studentId: String,
    termId: String
) extends Planner[List[Selection]]:
  def plan: IO[List[Selection]] =
    Utils.querySelections(studentId, termId)

case class UpdateStakeMessagePlanner(
    studentToken: String,
    termId: String,
    selectionId: String,
    points: Int
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      studentId <- CheckAuthMessage(studentToken, Role.Student).send
      _ <- Utils.checkTerm(termId)
      _ <- Utils.updateStake(
        studentId,
        selectionId,
        points
      )
    yield ()

case class UpdateSelectedCourseMessagePlanner(
    studentToken: String,
    termId: String,
    selectionId: String,
    courseId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      studentId <- CheckAuthMessage(studentToken, Role.Student).send
      _ <- Utils.checkTerm(termId)
      _ <- Utils.updateSelectedCourse(
        studentId,
        selectionId,
        courseId
      )
    yield ()

case class DeleteSelectionMessagePlanner(
    studentToken: String,
    termId: String,
    selectionId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      studentId <- CheckAuthMessage(studentToken, Role.Student).send
      _ <- Utils.checkTerm(termId)
      _ <- Utils.deleteSelection(
        studentId,
        selectionId
      )
    yield ()

case class ExecuteEnrollmentMessagePlanner(
    adminToken: String,
    termId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, Role.Admin).send
      _ <- Utils.checkTerm(termId)
      _ <- Utils.executeEnrollment(termId)
    yield ()
