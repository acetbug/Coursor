package Common.DBAPI

import Common.API.API

case class WriteDBListMessage(
    sqlStatement: String,
    parameters: List[ParameterList]
) extends API[String](DBService, "WriteDBListMessage")
