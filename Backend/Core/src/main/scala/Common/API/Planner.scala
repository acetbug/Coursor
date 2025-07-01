package Common.API

import Common.DBAPI.startTransaction
import cats.effect.IO
import io.circe.Encoder

trait Planner[ReturnType]:
  def plan: IO[ReturnType]

  def planWithErrorControl(using encoder: Encoder[ReturnType]): IO[ReturnType] =
    startTransaction:
      plan
    .onError: e =>
      IO.println("error:" + e)

  def fullPlan(using encoder: Encoder[ReturnType]): IO[ReturnType] =
    IO.println(this) >> planWithErrorControl(using encoder)
