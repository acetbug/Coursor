package APIs.TermService

import Common.API.API
import Global.ServiceCenter.TermService
import Objects.Term

case class QueryTermsMessage() extends API[List[Term]](TermService)
