package Objects

import io.circe._

case class Student(
    department: Option[Department],
    traces: List[Trace]
)

case class Department(
    id: String,
    name: String
)

case class Term(
    id: String,
    name: String,
    phase: Phase
)

case class Trace(
    term: Term,
    stage: Stage
)

enum Phase:
  case Enrolling
  case Confirmed

case object Phase:
  given Encoder[Phase] = Encoder.encodeString.contramap:
    case Phase.Enrolling => "Enrolling"
    case Phase.Confirmed => "Confirmed"

  given Decoder[Phase] = Decoder.decodeString.emap:
    case "Enrolling" => Right(Phase.Enrolling)
    case "Confirmed" => Right(Phase.Confirmed)
    case unknown     => Left(s"Unknown Phase: $unknown")

enum Stage:
  case Freshman_0
  case Freshman_1
  case Freshman_2
  case Sophomore_0
  case Sophomore_1
  case Sophomore_2
  case Junior_0
  case Junior_1
  case Junior_2
  case Senior_0
  case Senior_1
  case Senior_2

case object Stage:
  given Encoder[Stage] = Encoder.encodeString.contramap:
    case Stage.Freshman_0  => "Freshman_0"
    case Stage.Freshman_1  => "Freshman_1"
    case Stage.Freshman_2  => "Freshman_2"
    case Stage.Sophomore_0 => "Sophomore_0"
    case Stage.Sophomore_1 => "Sophomore_1"
    case Stage.Sophomore_2 => "Sophomore_2"
    case Stage.Junior_0    => "Junior_0"
    case Stage.Junior_1    => "Junior_1"
    case Stage.Junior_2    => "Junior_2"
    case Stage.Senior_0    => "Senior_0"
    case Stage.Senior_1    => "Senior_1"
    case Stage.Senior_2    => "Senior_2"

  given Decoder[Stage] = Decoder.decodeString.emap:
    case "Freshman_0"  => Right(Stage.Freshman_0)
    case "Freshman_1"  => Right(Stage.Freshman_1)
    case "Freshman_2"  => Right(Stage.Freshman_2)
    case "Sophomore_0" => Right(Stage.Sophomore_0)
    case "Sophomore_1" => Right(Stage.Sophomore_1)
    case "Sophomore_2" => Right(Stage.Sophomore_2)
    case "Junior_0"    => Right(Stage.Junior_0)
    case "Junior_1"    => Right(Stage.Junior_1)
    case "Junior_2"    => Right(Stage.Junior_2)
    case "Senior_0"    => Right(Stage.Senior_0)
    case "Senior_1"    => Right(Stage.Senior_1)
    case "Senior_2"    => Right(Stage.Senior_2)
    case unknown       => Left(s"Unknown Stage: $unknown")
