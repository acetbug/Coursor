package Common.DBAPI

import Common.API.API

case class EndTransactionMessage(commit: Boolean)
    extends API[String](DBService.service, "EndTransactionMessage")
