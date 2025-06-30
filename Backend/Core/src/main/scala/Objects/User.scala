package Objects

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

/** User desc: 用户信息，包括角色、部门等信息
  * @param id:
  *   String (用户的唯一ID)
  * @param name:
  *   String (用户姓名)
  * @param password:
  *   String (用户密码，加密后)
  * @param userRole:
  *   UserRole (用户角色，可能是学生、老师或管理员)
  */

case class User(
    id: String,
    name: String,
    password: String,
    userRole: UserRole
)

case object User:
  given Encoder[User] = deriveEncoder
  given Decoder[User] = deriveDecoder

enum UserRole:
  case Student // 学生
  case Teacher // 老师
  case Admin // 管理员

object UserRole:
  given encode: Encoder[UserRole] =
    Encoder.encodeString.contramap[UserRole](_.toString)
  given decode: Decoder[UserRole] = Decoder.decodeString.emap(fromString)

  def fromString(s: String): Either[String, UserRole] = try
    Right(UserRole.valueOf(s))
  catch case _: IllegalArgumentException => Left(s"Unknown UserRole: $s")
