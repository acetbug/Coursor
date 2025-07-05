package Common.DBAPI

import Common.API.API

case class ReadDBValueMessage(sqlQuery: String, parameters: List[SqlParameter])
    extends API[String](DBService, "ReadDBValueMessage")
