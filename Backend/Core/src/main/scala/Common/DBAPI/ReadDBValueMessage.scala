package Common.DBAPI

import Common.API.API
import Common.Object.SqlParameter

case class ReadDBValueMessage(sqlQuery: String, parameters: List[SqlParameter])
    extends API[String](DBService.service, "ReadDBValueMessage")
