package APIs.SelectionService

import Common.API.API
import Global.ServiceCenter.SelectionService

case class DeleteSelectionRecordMessage(
    studentToken: String,
    selectionRecordId: String
) extends API[Unit](SelectionService, "DeleteSelectionRecordMessage")
