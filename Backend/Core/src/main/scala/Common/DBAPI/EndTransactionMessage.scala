package Common.DBAPI

import Common.API.API

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class EndTransactionMessage(commit: Boolean)
    extends API[String](DBService.service)

object EndTransactionMessage:
  given Encoder[EndTransactionMessage] = deriveEncoder
