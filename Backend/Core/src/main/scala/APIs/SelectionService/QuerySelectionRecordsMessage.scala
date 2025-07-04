package APIs.SelectionService

import Common.API.API
import Global.SelectionService
import Objects.SelectionRecord

case class QuerySelectionRecordsMessage(
    studentId: String,
    termId: String
) extends API[List[SelectionRecord]](
      SelectionService,
      "QuerySelectionRecordsMessage"
    )
