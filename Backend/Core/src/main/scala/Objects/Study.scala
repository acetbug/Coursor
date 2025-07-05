package Objects

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

/** StudyTrace desc: 学生某学期学习情况
  * @param id:
  *   String (学业的唯一标识符)
  * @param studentId:
  *   String (学生的唯一标识符)
  * @param termId:
  *   String (学期的唯一标识符)
  * @param stage:
  *   String (学段)
  */

case class StudyTrace(
    id: String,
    studentId: String,
    termId: String,
    stage: StudyStage
)

case object StudyTrace:
  given Encoder[StudyTrace] = deriveEncoder
  given Decoder[StudyTrace] = deriveDecoder

enum StudyStage:
  case Freshman_0 // 大一小学期
  case Freshman_1 // 大一上学期
  case Freshman_2 // 大一下学期
  case Sophomore_0 // 大二小学期
  case Sophomore_1 // 大二上学期
  case Sophomore_2 // 大二下学期
  case Junior_0 // 大三小学期
  case Junior_1 // 大三上学期
  case Junior_2 // 大三下学期
  case Senior_0 // 大四小学期
  case Senior_1 // 大四上学期
  case Senior_2 // 大四下学期

case object StudyStage:
  given encode: Encoder[StudyStage] =
    Encoder.encodeString.contramap[StudyStage](_.toString)
  given decode: Decoder[StudyStage] = Decoder.decodeString.emap(fromString)

  def fromString(s: String): Either[String, StudyStage] = try
    Right(StudyStage.valueOf(s))
  catch case _: IllegalArgumentException => Left(s"Unknown StudyStage: $s")

/** StudyRecord desc: 学生的课程学习状态记录
  * @param id:
  *   String (学习记录的唯一标识符)
  * @param studentId:
  *   String (学生的唯一标识)
  * @param subjectId:
  *   String (科目的唯一标识)
  */

case class StudyRecord(
    id: String,
    studentId: String,
    subjectId: String
)

case object StudyRecord:
  given Encoder[StudyRecord] = deriveEncoder
  given Decoder[StudyRecord] = deriveDecoder
