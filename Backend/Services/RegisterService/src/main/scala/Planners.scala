import APIs.CheckAuthMessage
import Common.API.Planner
import Objects._

import cats.effect.IO
import io.circe.generic.auto._

case class CreateDepartmentMessagePlanner(
    adminToken: String,
    name: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, Role.Admin).send
      _ <- Utils.createDepartment(name)
    yield ()

case class CreateRegisterMessagePlanner(
    adminToken: String,
    studentId: String,
    departmentId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, Role.Admin).send
      _ <- Utils.createRegister(studentId, departmentId)
    yield ()

case class CreateTermMessagePlanner(
    adminToken: String,
    name: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, Role.Admin).send
      _ <- Utils.createTerm(name)
    yield ()

case class CreateTraceMessagePlanner(
    adminToken: String,
    studentId: String,
    termId: String,
    stage: Stage
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, Role.Admin).send
      _ <- Utils.createTrace(studentId, termId, stage)
    yield ()

case class QueryDepartmentsMessagePlanner() extends Planner[List[Department]]:
  def plan: IO[List[Department]] =
    Utils.queryDepartments

case class QueryTermsMessagePlanner() extends Planner[List[Term]]:
  def plan: IO[List[Term]] =
    Utils.queryTerms

case class QueryStudentMessagePlanner(
    studentId: String
) extends Planner[Student]:
  def plan: IO[Student] =
    Utils.queryStudent(studentId)

case class QueryStudentsMessagePlanner(
    departmentId: String
) extends Planner[List[User]]:
  def plan: IO[List[User]] =
    Utils.queryStudents(departmentId)

case class UpdateDepartmentMessagePlanner(
    adminToken: String,
    departmentId: String,
    name: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, Role.Admin).send
      _ <- Utils.updateDepartment(departmentId, name)
    yield ()

case class UpdateTermMessagePlanner(
    adminToken: String,
    termId: String,
    name: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, Role.Admin).send
      _ <- Utils.updateTerm(termId, name)
    yield ()

case class DeleteDepartmentMessagePlanner(
    adminToken: String,
    departmentId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, Role.Admin).send
      _ <- Utils.deleteDepartment(departmentId)
    yield ()

case class DeleteTermMessagePlanner(
    adminToken: String,
    termId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, Role.Admin).send
      _ <- Utils.deleteTerm(termId)
    yield ()

case class DeleteRegisterMessagePlanner(
    adminToken: String,
    studentId: String,
    departmentId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, Role.Admin).send
      _ <- Utils.deleteRegister(studentId, departmentId)
    yield ()

case class DeleteTraceMessagePlanner(
    adminToken: String,
    studentId: String,
    termId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, Role.Admin).send
      _ <- Utils.deleteTrace(studentId, termId)
    yield ()
