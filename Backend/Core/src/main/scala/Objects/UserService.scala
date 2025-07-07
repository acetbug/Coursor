package Objects

import io.circe._

case class UserAuth(
    token: String,
    name: String,
    role: Role
)

case class User(
    id: String,
    name: String
)

enum Role:
  case Student
  case Teacher
  case Admin

case object Role:
  given Encoder[Role] = Encoder.encodeString.contramap:
    case Role.Student => "Student"
    case Role.Teacher => "Teacher"
    case Role.Admin   => "Admin"

  given Decoder[Role] = Decoder.decodeString.emap:
    case "Student" => Right(Role.Student)
    case "Teacher" => Right(Role.Teacher)
    case "Admin"   => Right(Role.Admin)
    case unknown   => Left(s"Unknown Role: $unknown")
