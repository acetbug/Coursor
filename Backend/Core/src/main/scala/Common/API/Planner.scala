package Common.API

import cats.effect.IO
import io.circe.Encoder

trait Planner[T]:
  def plan: IO[T]
