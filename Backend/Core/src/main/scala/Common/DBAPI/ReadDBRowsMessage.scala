package Common.DBAPI

import Common.API.API

import io.circe.Json
case class ReadDBRowsMessage(sqlQuery: String, parameters: List[SqlParameter])
    extends API[List[Json]](DBService, "ReadDBRowsMessage")
