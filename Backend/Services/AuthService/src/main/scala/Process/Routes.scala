package Process

import Impl._

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.dsl.io._

object Routes:
  private def decodeAndExecute(messageType: String, str: String): IO[String] =
    messageType match
      case "CheckAuthMessage" =>
        decode[CheckAuthMessagePlanner](str) match
          case Left(err) =>
            IO.raiseError(
              new Exception(
                s"Invalid JSON for CheckAuthMessage[${err.getMessage}]"
              )
            )
          case Right(planner) => planner.plan.map(_.asJson.toString)

      case "CreateAuthMessage" =>
        decode[CreateAuthMessagePlanner](str) match
          case Left(err) =>
            IO.raiseError(
              new Exception(
                s"Invalid JSON for CreateAuthMessage[${err.getMessage}]"
              )
            )
          case Right(planner) => planner.plan.map(_.asJson.toString)

      case "DeleteAuthMessage" =>
        decode[DeleteAuthMessagePlanner](str) match
          case Left(err) =>
            IO.raiseError(
              new Exception(
                s"Invalid JSON for DeleteAuthMessage[${err.getMessage}]"
              )
            )
          case Right(planner) => planner.plan.map(_.asJson.toString)

      case _ =>
        IO.raiseError(new Exception(s"Unknown message type: $messageType"))

  def handlePostRequest(req: Request[IO]): IO[String] =
    req.as[Json].map(_.toString())

  val service: HttpRoutes[IO] = HttpRoutes.of[IO]:
    case GET -> Root / "health" =>
      Ok("OK")

    case req @ POST -> Root / "api" / name =>
      handlePostRequest(req)
        .flatMap(decodeAndExecute(name, _))
        .flatMap(Ok(_))
        .handleErrorWith:
          case e: Throwable =>
            BadRequest(e.getMessage.asJson.toString)
