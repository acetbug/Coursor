package Objects

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

/** Auth desc: 用户认证信息
  * @param token:
  *   String (用户认证令牌)
  * @param userRole:
  *   UserRole (用户角色)
  * @param expiresAt:
  *   Int (令牌过期时间)
  */

case class Auth(
    token: String,
    userRole: UserRole,
    expiresAt: Int
)

case object Auth:
  given Encoder[Auth] = deriveEncoder
  given Decoder[Auth] = deriveDecoder
