package APIs.StudyService

import Common.API.API
import Global.StudyService

case class DeleteStudyRecordMessage(
    adminToken: String,
    studyRecordId: String
) extends API[Unit](StudyService, "DeleteStudyRecordMessage")
