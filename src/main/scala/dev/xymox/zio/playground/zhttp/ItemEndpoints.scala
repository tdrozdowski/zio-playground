package dev.xymox.zio.playground.zhttp

import dev.xymox.zio.playground.quill.ItemService
import dev.xymox.zio.playground.quill.repository.NotFoundException
import zhttp.http._
import zio.Has
import zio.json._

object ItemEndpoints {

  val item: Http[Has[ItemService], HttpError, Request, UResponse] = Http
    .collectM[Request] {
      case Method.GET -> Root / "items"      =>
        for {
          items <- ItemService.all
        } yield Response.jsonString(items.toJson)
      case Method.GET -> Root / "items" / id =>
        for {
          item <- ItemService.get(id.toInt)
        } yield Response.jsonString(item.toJson)
    }
    .catchAll {
      case NotFoundException(msg, id) =>
        Http.fail(HttpError.NotFound(Root / "items" / id.toString))
      case ex                         =>
        Http.fail(HttpError.InternalServerError(msg = ex.getMessage, cause = Option(ex)))
    }
}
