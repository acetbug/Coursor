package Common.DBAPI

import Common.API.API

case class SwitchDataSourceMessage(projectName: String)
    extends API[String](DBService, "SwitchDataSourceMessage")
