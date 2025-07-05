package APIs.TermService

import Common.API.API
import Global.TermService
import Objects.Term

case class QueryTermsMessage(
    termIds: List[String]
) extends API[List[Term]](TermService, "QueryTermsMessage")
