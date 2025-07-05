package Common.DBAPI

import Common.API.API

case class StartTransactionMessage() extends API[String](DBService, "StartTransactionMessage")
