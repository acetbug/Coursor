package APIs.StudyService

import Common.API.API
import Global.StudyService
import Objects.StudyTrace

case class QueryStudyTracesMessage(
    studentId: String
) extends API[List[StudyTrace]](StudyService, "QueryStudyTracesMessage")
