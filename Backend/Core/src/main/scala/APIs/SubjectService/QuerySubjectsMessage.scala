package APIs.SubjectService

import Common.API.API
import Global.ServiceCenter.SubjectService
import Objects.Subject

case class QuerySubjectsMessage() extends API[List[Subject]](SubjectService)
