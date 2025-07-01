package APIs.TermService

import Common.API.API
import Global.ServiceCenter.TermService

case class DeleteTermMessage(
    adminToken: String,
    termId: String
) extends API[String](TermService)
