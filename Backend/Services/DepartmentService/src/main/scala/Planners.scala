import APIs.AuthService.CheckAuthMessage
import Common.API.Planner
import Objects._

import cats.effect.IO
import io.circe.generic.auto._

case class DeleteDepartmentMessagePlanner(
    adminToken: String,
    departmentId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      _ <- Utils.deleteDepartment(departmentId)
    yield ()

case class DeleteDepartmentSubjectRecommendationMessagePlanner(
    adminToken: String,
    departmentSubjectRecommendationId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      _ <- Utils.deleteDepartmentSubjectRecommendation(
        departmentSubjectRecommendationId
      )
    yield ()

case class QueryDepartmentsMessagePlanner(
    departmentIds: List[String]
) extends Planner[List[Department]]:
  def plan: IO[List[Department]] =
    Utils.queryDepartments(departmentIds)

case class QueryDepartmentSubjectRecommendationsMessagePlanner(
    departmentId: String,
    studyStage: StudyStage
) extends Planner[List[DepartmentSubjectRecommendation]]:
  def plan: IO[List[DepartmentSubjectRecommendation]] =
    Utils.queryDepartmentSubjectRecommendations(departmentId, studyStage)

case class UpdateDepartmentMessagePlanner(
    adminToken: String,
    department: Department
) extends Planner[String]:
  def plan: IO[String] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      departmentId <- Utils.updateDepartment(department)
    yield departmentId

case class UpdateDepartmentSubjectRecommendationMessagePlanner(
    adminToken: String,
    departmentSubjectRecommendation: DepartmentSubjectRecommendation
) extends Planner[String]:
  def plan: IO[String] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      departmentId <- Utils.updateDepartmentSubjectRecommendation(
        departmentSubjectRecommendation
      )
    yield departmentId
