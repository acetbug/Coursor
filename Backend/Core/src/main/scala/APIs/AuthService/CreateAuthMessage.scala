package APIs.AuthService

import Common.API.API
import Global.AuthService
import Objects.Auth

case class CreateAuthMessage(
    userId: String,
    password: String
) extends API[Auth](AuthService, "CreateAuthMessage")
