package APIs.AuthService

import Common.API.API
import Global.ServiceCenter.AuthService
import Objects.Auth

case class CreateAuthMessage(
    userId: String,
    password: String
) extends API[Auth](AuthService, "CreateAuthMessage")
