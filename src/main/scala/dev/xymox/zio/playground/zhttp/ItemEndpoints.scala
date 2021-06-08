package dev.xymox.zio.playground.zhttp

import dev.xymox.zio.playground.quill.repository.NotFoundException
import dev.xymox.zio.playground.quill.{CreateItemRequest, ItemService}
import pdi.jwt.JwtClaim
import zhttp.http._
import zio.console._
import zio.json._
import zio.{Has, IO, ZIO}

object ItemEndpoints extends RequestOps {

  val item: JwtClaim => Http[Has[ItemService] with Console, HttpError, Request, UResponse] = jwtClaim =>
    Http
      .collectM[Request] {
        case Method.GET -> Root / "items"        =>
          for {
            _     <- putStrLn(s"Validated claim: $jwtClaim")
            items <- ItemService.all
          } yield Response.jsonString(items.toJson)
        case Method.GET -> Root / "items" / id   =>
          for {
            _    <- putStrLn(s"Validated claim: $jwtClaim")
            item <- ItemService.get(id.toInt)
          } yield Response.jsonString(item.toJson)
        case req @ Method.POST -> Root / "items" =>
          for {
            _       <- putStrLn(s"Validated claim: $jwtClaim")
            request <- extractBodyFromJson[CreateItemRequest](req)
            results <- ItemService.create(request)
          } yield Response.jsonString(results.toJson)
      }
      .catchAll {
        case NotFoundException(msg, id) =>
          Http.fail(HttpError.NotFound(Root / "items" / id.toString))
        case ex: Throwable              =>
          Http.fail(HttpError.InternalServerError(msg = ex.getMessage, cause = Option(ex)))
        case err                        => Http.fail(HttpError.InternalServerError(msg = err.toString))
      }
}

trait RequestOps {

  def extractBodyFromJson[A](request: Request)(implicit codec: JsonCodec[A]): IO[Serializable, A] =
    for {
      requestOrError <- ZIO.fromOption(request.getBodyAsString.map(_.fromJson[A]))
      body           <- ZIO.fromEither(requestOrError)
    } yield body
}
