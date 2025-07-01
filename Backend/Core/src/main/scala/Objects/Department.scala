package Objects

import Objects.StudyStage

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

/** Department desc: 学院信息，包含学院的基本信息和培养需求
  * @param id:
  *   String (学院的唯一ID)
  * @param name:
  *   String (学院名称)
  */

case class Department(
    id: String,
    name: String
)

case object Department:
  given Encoder[Department] = deriveEncoder
  given Decoder[Department] = deriveDecoder

/** DepartmentSubjectRecommendation desc: 学科推荐信息，包含学科ID和推荐级别
  * @param id:
  *   String (学科推荐的唯一ID)
  * @param departmentId:
  *   String (学院的唯一ID)
  * @param subjectId:
  *   String (学科的唯一ID)
  * @param studyStage:
  *   StudyStage (学段)
  * @param level:
  *   Int (推荐级别)
  */

case class DepartmentSubjectRecommendation(
    id: String,
    departmentId: String,
    subjectId: String,
    studyStage: StudyStage,
    level: Int // TODO: 推荐级别的具体定义
)

case object DepartmentSubjectRecommendation:
  given Encoder[DepartmentSubjectRecommendation] =
    deriveEncoder
  given Decoder[DepartmentSubjectRecommendation] =
    deriveDecoder
