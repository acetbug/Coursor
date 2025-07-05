package Process

import Common.API.API
import Common.DBAPI._

import cats.effect._
import com.comcast.ip4s._
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.dsl.io._
import org.http4s.server.Server
import org.http4s.server.middleware.CORS
import scala.concurrent.duration.DurationInt

object Server:
  def init: IO[Unit] =
    for
      _ <- API.init
      _ <- SwitchDataSourceMessage("coursor").send
      _ <- StartTransactionMessage().send
    yield ()

  def service(handleRequest: (String, Json) => IO[Json]): HttpRoutes[IO] =
    HttpRoutes.of[IO]:
      case GET -> Root / "health" =>
        Ok(Json.fromString("Ok"))

      case req @ POST -> Root / "api" / name =>
        (for
          requestJson <- req.as[Json]
          responseJson <- handleRequest(name, requestJson)
          response <- Ok(responseJson)
        yield response)
          .handleErrorWith: e =>
            BadRequest(e.getMessage.asJson)

  def app(service: HttpRoutes[IO]): HttpApp[IO] =
    CORS.policy.withAllowOriginAll
      .withAllowMethodsIn(Set(Method.POST))
      .apply(service.orNotFound)

  def server(port: Int, app: HttpApp[IO]): Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withHost(Host.fromString("0.0.0.0").get)
      .withPort(Port.fromInt(port).get)
      .withIdleTimeout(30.minutes)
      .withShutdownTimeout(30.minutes)
      .withRequestHeaderReceiveTimeout(30.minutes)
      .withMaxConnections(10000)
      .withHttpApp(app)
      .build
