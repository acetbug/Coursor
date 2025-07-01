package APIs.AuthService

import Common.API.API
import Global.ServiceCenter.AuthService
import Objects.Auth

case class QueryAuthMessage(
    token: String
) extends API[Auth](AuthService)
