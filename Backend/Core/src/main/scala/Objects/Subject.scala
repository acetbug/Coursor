package Objects

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

/** Subject desc: 科目信息，包含指定科目id、名称以及前置和允许的阶段
  * @param id:
  *   String (科目唯一标识符)
  * @param name:
  *   String (科目名称)
  * @param credits:
  *   Int (科目学分)
  */

case class Subject(
    id: String,
    name: String,
    credits: Int
)

case object Subject:
  given Encoder[Subject] = deriveEncoder
  given Decoder[Subject] = deriveDecoder
