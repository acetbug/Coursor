package APIs.SelectionService

import Common.API.API
import Global.ServiceCenter.SelectionService
import Objects.SelectionRecord

case class UpdateSelectionRecordMessage(
    studentToken: String,
    selectionRecord: SelectionRecord
) extends API[String](SelectionService)
