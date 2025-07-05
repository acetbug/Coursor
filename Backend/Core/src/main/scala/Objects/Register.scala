package Objects

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

/** Register desc: 学生注册信息
  * @param id:
  *   String (注册的唯一标识符)
  * @param studentId:
  *   String (学生的唯一标识符)
  * @param departmentId:
  *   String (学院的唯一标识符)
  */

case class Register(
    id: String,
    studentId: String,
    departmentId: String
)

case object Register:
  given Encoder[Register] = deriveEncoder
  given Decoder[Register] = deriveDecoder
