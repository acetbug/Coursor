package APIs.TermService

import Common.API.API
import Global.ServiceCenter.TermService
import Objects.Term

case class UpdateTermMessage(
    adminToken: String,
    term: Term
) extends API[String](TermService)
