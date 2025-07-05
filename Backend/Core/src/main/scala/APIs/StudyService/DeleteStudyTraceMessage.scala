package APIs.StudyService

import Common.API.API
import Global.StudyService
import Objects.StudyTrace

case class DeleteStudyTraceMessage(
    adminToken: String,
    studyTraceId: String
) extends API[Unit](StudyService, "DeleteStudyTraceMessage")
