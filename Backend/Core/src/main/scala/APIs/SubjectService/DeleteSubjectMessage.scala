package APIs.SubjectService

import Common.API.API
import Global.ServiceCenter.SubjectService

case class DeleteSubjectMessage(
    adminToken: String,
    subjectId: String
) extends API[Unit](SubjectService, "DeleteSubjectMessage")
