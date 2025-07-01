package APIs.DepartmentService

import Common.API.API
import Global.ServiceCenter.DepartmentService

case class DeleteDepartmentSubjectRecommendationMessage(
    adminToken: String,
    departmentSubjectRecommendationId: String
) extends API[String](DepartmentService)
