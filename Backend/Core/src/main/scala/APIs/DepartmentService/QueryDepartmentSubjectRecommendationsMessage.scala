package APIs.DepartmentService

import Common.API.API
import Global.DepartmentService
import Objects.DepartmentSubjectRecommendation
import Objects.StudyStage

case class QueryDepartmentSubjectRecommendationsMessage(
    departmentId: String,
    studyStage: StudyStage
) extends API[List[DepartmentSubjectRecommendation]](
      DepartmentService,
      "QueryDepartmentSubjectRecommendationsMessage"
    )
