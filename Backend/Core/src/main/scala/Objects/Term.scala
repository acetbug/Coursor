package Objects

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

/** Term desc: 学期信息表，包含基本的学期时间以及ID名称
  * @param id:
  *   String (学期的唯一标识符)
  * @param name:
  *   String (学期的名称) // TODO: 类型化
  * @param phase:
  *   TermPhase (学期的当前阶段)
  */

case class Term(
    id: String,
    name: String,
    phase: TermPhase
)

case object Term:
  given Encoder[Term] = deriveEncoder
  given Decoder[Term] = deriveDecoder

enum TermPhase:
  case NotStarted // 尚未开始
  case PreSelection // 预选阶段
  case Lottery // 抽签阶段
  case MainSelection // 主选阶段
  case MidTermWithdrawal // 期中退课阶段
  case Confirmed // 确认阶段

object TermPhase:
  given Encoder[TermPhase] = Encoder.encodeString.contramap(_.toString)
  given Decoder[TermPhase] = Decoder.decodeString.emap(fromString)

  def fromString(s: String): Either[String, TermPhase] = try
    Right(TermPhase.valueOf(s))
  catch case _: IllegalArgumentException => Left(s"Unknown TermPhase: $s")
