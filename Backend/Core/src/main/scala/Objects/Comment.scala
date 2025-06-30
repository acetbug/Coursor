package Objects

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

/** Comment desc: 学生对课程的评论信息
  * @param id:
  *   String (评论的唯一ID)
  * @param studentId:
  *   String (发表评论的学生ID)
  * @param courseId:
  *   String (评论的课程ID)
  * @param content:
  *   String (评论的具体内容)
  */

case class Comment(
    id: String,
    studentId: String,
    courseId: String,
    content: String
)

object Comment:
  given Encoder[Comment] = deriveEncoder
  given Decoder[Comment] = deriveDecoder
