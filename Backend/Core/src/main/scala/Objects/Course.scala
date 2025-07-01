package Objects

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

/** Course desc: 课程信息
  * @param id:
  *   String (课程的唯一ID)
  * @param subjectId:
  *   String (课程关联的学科ID)
  * @param teacherId:
  *   String (授课教师的唯一ID)
  * @param termId:
  *   String (课程所在学期的唯一ID)
  * @param location:
  *   String (课程的授课地点) // TODO: 地点对象
  * @param schedule:
  *   String (课程的授课时间) // TODO: 时间对象
  * @param capacity:
  *   Int (课程的最大容量)
  */

case class Course(
    id: String,
    subjectId: String,
    teacherId: String,
    termId: String,
    location: String,
    schedule: String,
    capacity: Int
)

case object Course:
  given Encoder[Course] = deriveEncoder
  given Decoder[Course] = deriveDecoder
