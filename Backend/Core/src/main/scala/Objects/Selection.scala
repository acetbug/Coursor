package Objects

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

/** SelectionRecord desc: 选课记录信息，包含学生、课程和状态等。
  * @param id:
  *   String (选课记录的唯一标识)
  * @param studentId:
  *   String (学生的唯一标识)
  * @param courseId:
  *   String (课程的唯一标识)
  * @param points:
  *   Int (分配的选课点数)
  */

case class SelectionRecord(
    id: String,
    studentId: String,
    courseId: String,
    points: Int
)

case object SelectionRecord:
  given Encoder[SelectionRecord] = deriveEncoder
  given Decoder[SelectionRecord] = deriveDecoder
