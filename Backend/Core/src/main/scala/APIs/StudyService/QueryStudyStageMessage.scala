package APIs.StudyService

import Common.API.API
import Global.ServiceCenter.StudyService
import Objects.StudyStage

case class QueryStudyStageMessage(
    studentId: String,
    termId: String
) extends API[StudyStage](StudyService, "QueryStudyStageMessage")
