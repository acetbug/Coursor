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
  type ReturnType = T

  def getURIWithAPIMessageName: IO[Uri] =
    IO.fromEither(
      Uri.fromString(
        s"${targetService.getUri}${name}"
      )
    )

  def send(using Encoder[this.type]): IO[T] =
    API.send[T, this.type](this)

object API:
  trait ResponseHandler[T]:
    def handle(response: Response[IO]): IO[T]

  given ResponseHandler[String] with
    def handle(response: Response[IO]): IO[String] =
      response.bodyText.compile.string

  given [T: Decoder]: ResponseHandler[T] with
    def handle(response: Response[IO]): IO[T] =
      response
        .asJsonDecode[T]
        .flatMap:
          IO(_)

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
          response.status match
            case status if status.isSuccess =>
              summon[ResponseHandler[T]].handle(response)

            case _ =>
              summon[ResponseHandler[String]]
                .handle(response)
                .flatMap: rawJsonString =>
                  val error = parse(rawJsonString) match
                    case Left(_) => new Exception(rawJsonString)
                    case Right(json) =>
                      json.as[String] match
                        case Left(_)        => new Exception(rawJsonString)
                        case Right(message) => new Exception(message)

                  IO.raiseError(error)
    yield result
