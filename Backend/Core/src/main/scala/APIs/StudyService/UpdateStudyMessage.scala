package APIs.StudyService

import Common.API.API
import Global.ServiceCenter.StudyService
import Objects.Study

case class UpdateStudyMessage(
    adminToken: String,
    study: Study
) extends API[String](StudyService)
