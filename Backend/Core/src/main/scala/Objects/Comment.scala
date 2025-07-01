package Objects

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

/** Comment desc: 学生对课程的评论信息
  * @param id:
  *   String (评论的唯一ID)
  * @param studentId:
  *   String (发表评论的学生ID)
  * @param teacherId:
  *   String (课程的教师ID)
  * @param subjectId:
  *   String (课程的学科ID)
  * @param content:
  *   String (评论的具体内容)
  */

case class Comment(
    id: String,
    studentId: String,
    teacherId: String,
    subjectId: String,
    content: String
)

case object Comment:
  given Encoder[Comment] = deriveEncoder
  given Decoder[Comment] = deriveDecoder
