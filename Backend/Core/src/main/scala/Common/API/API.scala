package Common.API

import Global.Service

import cats.effect._
import io.circe._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import scala.concurrent.duration.DurationInt

/** API的基本类型，保存了API返回的数据类型 ReturnType */
abstract class API[T: Decoder](targetService: Service):
  type ReturnType = T

  def getURIWithAPIMessageName: IO[Uri] =
    IO.fromEither(
      Uri.fromString(
        s"${targetService.getUri}${this.getClass.getSimpleName}"
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
    def handle(response: Response[IO]): IO[T] = {
      response.asJsonDecode[T].flatMap:
        IO(_)
    }

  private var client: Option[Client[IO]] = None

  def init: IO[Unit] = {
    val clientResource: Resource[IO, Client[IO]] = EmberClientBuilder
      .default[IO]
      .withMaxTotal(10000)
      .withTimeout(30.seconds)
      .withIdleConnectionTime(30.seconds)
      .build

    clientResource.use: httpClient =>
      IO:
        client = Some(httpClient)
  }

  def send[T: Decoder, A <: API[T]: Encoder](message: A): IO[T] =
    for {
      uri <- message.getURIWithAPIMessageName

      request = Request[IO](Method.POST, uri).withEntity(message.asJson)

      result <- client.get.run(request).use { response =>
        response.status match {
          case status if status.isSuccess =>
            response.bodyText.compile.string.flatMap { body =>
              IO.println(s"Response body: $body")
            } >>
              summon[ResponseHandler[T]].handle(response)
          case _ =>
            response.bodyText.compile.string.flatMap { body =>
              IO.raiseError(
                new Exception(
                  s"Unexpected response status: ${response.status.code}, body: $body"
                )
              )
            }
        }
      }
    } yield result
