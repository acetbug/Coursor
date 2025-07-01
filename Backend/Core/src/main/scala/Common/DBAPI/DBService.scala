package Common.DBAPI

import Global.Service

object DBService:
  val service: Service = new Service(10002)
