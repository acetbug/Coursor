package Common.DBAPI

import Common.API.API

case class InitSchemaMessage(schemaName: String)
    extends API[String](DBService.service, "InitSchemaMessage")
