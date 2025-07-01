package Common.DBAPI

import Common.API.API

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class StartTransactionMessage() extends API[String](DBService.service)

object StartTransactionMessage:
  given Encoder[StartTransactionMessage] = deriveEncoder
