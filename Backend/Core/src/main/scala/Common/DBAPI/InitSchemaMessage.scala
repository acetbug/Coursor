package Common.DBAPI

import Common.API.API

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class InitSchemaMessage(schemaName: String)
    extends API[String](DBService.service)

object InitSchemaMessage:
  given Encoder[InitSchemaMessage] = deriveEncoder
