import APIs.AuthService.CheckAuthMessage
import Common.API.Planner
import Objects._

import cats.effect.IO
import io.circe.generic.auto._

case class DeleteRegisterMessagePlanner(
    adminToken: String,
    registerId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      _ <- Utils.deleteRegister(registerId)
    yield ()

case class QueryRegistersByDepartmentMessagePlanner(
    departmentId: String
) extends Planner[List[Register]]:
  def plan: IO[List[Register]] =
    Utils.queryRegistersByDepartment(departmentId)

case class QueryRegistersByStudentMessagePlanner(
    studentId: String
) extends Planner[List[Register]]:
  def plan: IO[List[Register]] =
    Utils.queryRegistersByStudent(studentId)

case class UpdateRegisterMessagePlanner(
    adminToken: String,
    register: Register
) extends Planner[String]:
  def plan: IO[String] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      registerId <- Utils.updateRegister(register)
    yield registerId
