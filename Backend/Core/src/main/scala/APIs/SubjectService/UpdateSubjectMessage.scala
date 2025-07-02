package APIs.SubjectService

import Common.API.API
import Global.ServiceCenter.SubjectService
import Objects.Subject

case class UpdateSubjectMessage(
    adminToken: String,
    subject: Subject
) extends API[String](SubjectService, "UpdateSubjectMessage")
