package Common.DBAPI

import Common.API.API
import Common.Object.ParameterList

case class WriteDBListMessage(
    sqlStatement: String,
    parameters: List[ParameterList]
) extends API[String](DBService.service, "WriteDBListMessage")
