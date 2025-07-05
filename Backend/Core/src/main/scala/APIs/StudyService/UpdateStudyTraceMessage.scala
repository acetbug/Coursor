package APIs.StudyService

import Common.API.API
import Global.StudyService
import Objects.StudyTrace

case class UpdateStudyTraceMessage(
    adminToken: String,
    studyTrace: StudyTrace
) extends API[String](StudyService, "UpdateStudyTraceMessage")
