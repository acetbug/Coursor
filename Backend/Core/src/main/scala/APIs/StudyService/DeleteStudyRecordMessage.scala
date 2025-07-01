package APIs.StudyService

import Common.API.API
import Global.ServiceCenter.StudyService
import Objects.StudyStage

case class DeleteStudyRecordMessage(
    studentToken: String,
    studyRecordId: String
) extends API[String](StudyService)
