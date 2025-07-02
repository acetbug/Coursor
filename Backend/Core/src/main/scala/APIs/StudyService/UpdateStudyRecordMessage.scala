package APIs.StudyService

import Common.API.API
import Global.ServiceCenter.StudyService
import Objects.StudyRecord

case class UpdateStudyRecordMessage(
    studentToken: String,
    studyRecord: StudyRecord
) extends API[String](StudyService, "UpdateStudyRecordMessage")
