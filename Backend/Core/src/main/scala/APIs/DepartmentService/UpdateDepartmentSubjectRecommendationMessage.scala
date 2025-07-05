package APIs.DepartmentService

import Common.API.API
import Global.DepartmentService
import Objects.DepartmentSubjectRecommendation

case class UpdateDepartmentSubjectRecommendationMessage(
    adminToken: String,
    departmentSubjectRecommendation: DepartmentSubjectRecommendation
) extends API[String](
      DepartmentService,
      "UpdateDepartmentSubjectRecommendationMessage"
    )
