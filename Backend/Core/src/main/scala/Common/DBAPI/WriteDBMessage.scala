package Common.DBAPI

import Common.API.API

case class WriteDBMessage(sqlStatement: String, parameters: List[SqlParameter])
    extends API[String](DBService, "WriteDBMessage")
