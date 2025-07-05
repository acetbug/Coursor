package APIs.DepartmentService

import Common.API.API
import Global.DepartmentService

case class DeleteDepartmentSubjectRecommendationMessage(
    adminToken: String,
    departmentSubjectRecommendationId: String
) extends API[Unit](
      DepartmentService,
      "DeleteDepartmentSubjectRecommendationMessage"
    )
