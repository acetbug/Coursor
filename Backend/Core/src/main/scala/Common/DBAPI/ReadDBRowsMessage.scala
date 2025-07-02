package Common.DBAPI

import Common.API.API
import Common.Object.SqlParameter

import io.circe.Json
case class ReadDBRowsMessage(sqlQuery: String, parameters: List[SqlParameter])
    extends API[List[Json]](DBService.service, "ReadDBRowsMessage")
