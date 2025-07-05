package Global

import cats.effect.IO
import org.http4s.Uri

trait Service(val port: Int, val schema: String):
  def getUri: String =
    s"http://localhost:$port/api/"
