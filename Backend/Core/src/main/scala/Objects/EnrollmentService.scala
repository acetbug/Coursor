package Objects

import io.circe._

case class EnrollmentGroup(
    kind: Kind,
    enrollments: List[Enrollment]
)

case class Enrollment(
    subject: Subject,
    selection: Option[Selection]
)

case class Selection(
    stake: Stake,
    course: Option[Course]
)

case class Stake(
    id: String,
    points: Int
)

enum Kind:
  case Postponed
  case Recommended
  case Enrolled

case object Kind:
  given Encoder[Kind] = Encoder.encodeString.contramap:
    case Kind.Postponed   => "Postponed"
    case Kind.Recommended => "Recommended"
    case Kind.Enrolled    => "Enrolled"

  given Decoder[Kind] = Decoder.decodeString.emap:
    case "Postponed"   => Right(Kind.Postponed)
    case "Recommended" => Right(Kind.Recommended)
    case "Enrolled"    => Right(Kind.Enrolled)
    case unknown       => Left(s"Unknown Kind: $unknown")
