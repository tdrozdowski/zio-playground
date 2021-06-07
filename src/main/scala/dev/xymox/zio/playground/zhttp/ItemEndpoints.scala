package dev.xymox.zio.playground.zhttp

import dev.xymox.zio.playground.quill.ItemService
import zhttp.http._
import zio.Has
import zio.json._

object ItemEndpoints {

  val item: Http[Has[ItemService], Throwable, Any, UResponse] = Http.collectM {
    case Method.GET -> Root / "items"      =>
      for {
        items <- ItemService.all
      } yield Response.jsonString(items.toJson)
    case Method.GET -> Root / "items" / id =>
      for {
        item <- ItemService.get(id.toInt)
      } yield Response.jsonString(item.toJson)
  }
}
