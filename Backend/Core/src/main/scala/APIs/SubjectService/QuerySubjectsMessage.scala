package APIs.SubjectService

import Common.API.API
import Global.SubjectService
import Objects.Subject

case class QuerySubjectsMessage(
    subjectIds: List[String]
) extends API[List[Subject]](SubjectService, "QuerySubjectsMessage")
