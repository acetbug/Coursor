package Process

import Init.initDB
import Routes.service

import Common.Init.init
import Utils.Constants

import cats.effect._
import com.comcast.ip4s._
import java.nio.channels.ClosedChannelException
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.CORS
import scala.concurrent.duration.DurationInt

object Server extends IOApp:
  val app: HttpApp[IO] = CORS.policy.withAllowOriginAll(service.orNotFound)

  override protected def reportFailure(err: Throwable): IO[Unit] =
    err match
      case e: ClosedChannelException =>
        IO.unit
      case _ =>
        super.reportFailure(err)

  def run(args: List[String]): IO[ExitCode] =
    for
      _ <- init

      _ <- initDB

      exitCode <- EmberServerBuilder
        .default[IO]
        .withHost(Host.fromString(Constants.host).get)
        .withPort(Port.fromInt(Constants.port).get)
        .withIdleTimeout(30.minutes)
        .withShutdownTimeout(30.minutes)
        .withRequestHeaderReceiveTimeout(30.minutes)
        .withMaxConnections(10000)
        .withHttpApp(app)
        .build
        .use(_ => IO.never)
        .as(ExitCode.Success)
    yield exitCode
