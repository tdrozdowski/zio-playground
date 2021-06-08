package dev.xymox.zio.playground.zhttp

import dev.xymox.zio.playground.quill.{CreateItemRequest, Item, ItemService}
import dev.xymox.zio.playground.quill.repository.NotFoundException
import zhttp.http._
import zio.{Has, ZIO}
import zio.json._

object ItemEndpoints {

  val item: Http[Has[ItemService], HttpError, Request, UResponse] = Http
    .collectM[Request] {
      case Method.GET -> Root / "items"        =>
        for {
          items <- ItemService.all
        } yield Response.jsonString(items.toJson)
      case Method.GET -> Root / "items" / id   =>
        for {
          item <- ItemService.get(id.toInt)
        } yield Response.jsonString(item.toJson)
      case req @ Method.POST -> Root / "items" =>
        for {
          requestOrError <- ZIO.fromOption(req.getBodyAsString.map(_.fromJson[CreateItemRequest]))
          request        <- ZIO.fromEither(requestOrError)
          results        <- ItemService.create(request)
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
