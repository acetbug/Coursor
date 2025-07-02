package APIs.StudyService

import Common.API.API
import Global.ServiceCenter.StudyService
import Objects.Study

case class DeleteStudyMessage(
    adminToken: String,
    studyId: String
) extends API[Unit](StudyService, "DeleteStudyMessage")
