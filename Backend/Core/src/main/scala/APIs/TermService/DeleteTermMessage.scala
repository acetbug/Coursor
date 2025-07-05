package APIs.TermService

import Common.API.API
import Global.TermService

case class DeleteTermMessage(
    adminToken: String,
    termId: String
) extends API[Unit](TermService, "DeleteTermMessage")
