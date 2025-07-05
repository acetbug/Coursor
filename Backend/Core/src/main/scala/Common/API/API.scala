package Common.API

import Global.Service

import cats.effect._
import io.circe._
import io.circe.syntax._
import io.circe.parser._
import java.util.UUID
import org.http4s._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import scala.concurrent.duration.DurationInt

abstract class API[T: Decoder](targetService: Service, name: String):
  def getURIWithAPIMessageName: IO[Uri] =
    IO.fromEither(
      Uri.fromString(
        s"${targetService.getUri}${name}"
      )
    )

  def send(using Encoder[this.type]): IO[T] =
    API.send[T, this.type](this)

object API:
  private def handle[T: Decoder](response: Response[IO]): IO[T] =
    for result <- response.asJsonDecode[T]
    yield result

  private var client: Option[Client[IO]] = None

  def init: IO[Unit] =
    val clientResource: Resource[IO, Client[IO]] =
      EmberClientBuilder
        .default[IO]
        .withMaxTotal(10000)
        .withTimeout(30.seconds)
        .withIdleConnectionTime(30.seconds)
        .build

    clientResource.use: httpClient =>
      IO:
        client = Some(httpClient)

  def send[T: Decoder, A <: API[T]: Encoder](message: A): IO[T] =
    for
      uri <- message.getURIWithAPIMessageName

      modifiedJson = message.asJson.mapObject: obj =>
        val planContext = Json.obj(
          "traceID" -> Json.fromString(UUID.randomUUID().toString),
          "transactionLevel" -> Json.fromInt(0)
        )
        obj.add("planContext", planContext)

      request = Request[IO](Method.POST, uri).withEntity(modifiedJson)

      result <- client.get
        .run(request)
        .use: response =>
          response match
            case response if response.status.isSuccess =>
              handle[T](response)

            case _ =>
              handle[String](response)
                .flatMap: errorMessage =>
                  IO.raiseError(new Exception(errorMessage))
    yield result
