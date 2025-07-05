package APIs.SelectionService

import Common.API.API
import Global.SelectionService

case class ExecuteLotteryMessage(
    adminToken: String,
    termId: String
) extends API[Unit](SelectionService, "ExecuteLotteryMessage")
