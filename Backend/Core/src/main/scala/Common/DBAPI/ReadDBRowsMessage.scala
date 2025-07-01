package Common.DBAPI

import Common.API.API
import Common.Object.SqlParameter

import io.circe.Json
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class ReadDBRowsMessage(sqlQuery: String, parameters: List[SqlParameter])
    extends API[List[Json]](DBService.service)

object ReadDBRowsMessage:
  given Encoder[ReadDBRowsMessage] = deriveEncoder
