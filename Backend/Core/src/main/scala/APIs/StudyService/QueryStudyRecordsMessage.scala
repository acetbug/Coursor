package APIs.StudyService

import Common.API.API
import Global.StudyService
import Objects.StudyRecord

case class QueryStudyRecordsMessage(
    studentId: String
) extends API[List[StudyRecord]](StudyService, "QueryStudyRecordsMessage")
