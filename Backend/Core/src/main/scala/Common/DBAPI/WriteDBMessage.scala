package Common.DBAPI

import Common.API.API
import Common.Object.SqlParameter

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class WriteDBMessage(sqlStatement: String, parameters: List[SqlParameter])
    extends API[String](DBService.service)

object WriteDBMessage:
  given Encoder[WriteDBMessage] = deriveEncoder
