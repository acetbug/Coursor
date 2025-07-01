package APIs.SelectionService

import Common.API.API
import Global.ServiceCenter.SelectionService

case class ExecuteLotteryMessage(
    adminToken: String,
    termId: String
) extends API[String](SelectionService)
