package Common.API

import cats.effect.IO
import io.circe._
import io.circe.syntax._

trait Planner[R]:
  def plan: IO[R]

object Planner:
  def execute[T <: Planner[R], R](
      request: Json
  )(using Decoder[T], Encoder[R]): IO[Json] =
    for
      planner <- IO.fromEither(request.as[T])
      result <- planner.plan
      response <- IO.pure(result.asJson)
    yield response
