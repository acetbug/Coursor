package Global

import cats.effect.IO
import org.http4s.Uri

case class Service(port: Int):
  def getUri: String =
    s"http://localhost:$port/api/"
