package APIs.SelectionService

import Common.API.API
import Global.SelectionService

case class DeleteSelectionRecordMessage(
    studentToken: String,
    selectionRecordId: String
) extends API[Unit](SelectionService, "DeleteSelectionRecordMessage")
