package Objects

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import java.time.Instant

/** Auth desc: 用户认证信息
  * @param id:
  *   String (认证信息的唯一标识符)
  * @param userId:
  *   String (用户的唯一ID)
  * @param token:
  *   String (用户认证令牌)
  * @param expiresAt:
  *   Instant (令牌过期时间)
  */

case class Auth(
    id: String,
    userId: String,
    token: String,
    expiresAt: Instant
)
