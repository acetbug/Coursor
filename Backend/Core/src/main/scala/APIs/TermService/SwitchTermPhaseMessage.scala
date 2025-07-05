package APIs.TermService

import Common.API.API
import Global.TermService

case class SwitchTermPhaseMessage(
    adminToken: String,
    termId: String
) extends API[Unit](TermService, "SwitchTermPhaseMessage")
