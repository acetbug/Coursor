package APIs.StudyService

import Common.API.API
import Global.StudyService
import Objects.StudyRecord

case class UpdateStudyRecordMessage(
    adminToken: String,
    studyRecord: StudyRecord
) extends API[String](StudyService, "UpdateStudyRecordMessage")
