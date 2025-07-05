import Process.Server

import cats.effect._

object Main extends IOApp:
  def run(args: List[String]): IO[ExitCode] =
    val service = Server.service(Utils.handleRequest)
    val app = Server.app(service)
    val server = Server.server(Utils.thisService.port, app)

    for
      _ <- Server.init
      _ <- Utils.init
      exitCode <- server.useForever.as(ExitCode.Success)
    yield exitCode
