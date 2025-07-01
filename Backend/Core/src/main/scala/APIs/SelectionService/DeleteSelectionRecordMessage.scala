package APIs.SelectionService

import Common.API.API
import Global.ServiceCenter.SelectionService

case class AddSelectionRecordMessage(
    studentToken: String,
    selectionRecordId: String
) extends API[String](SelectionService)
