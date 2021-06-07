package dev.xymox.zio.playground.zhttp

import dev.xymox.zio.playground.quill.{ItemService, ZioQuillContext}
import dev.xymox.zio.playground.quill.repository.{ItemRepository, NotFoundException}
import zhttp.http._
import zhttp.service.Server
import zio._
import zio.console._
import zio.magic._

object ItemServer extends App {

  val endpoints: Http[Has[ItemService], Throwable, Any, UResponse] = ItemEndpoints.item
//    .catchAll {
//    case NotFoundException(msg, id) =>
//      Http.fail(Response.fromHttpError(HttpError.NotFound(Root / "items" / id.toString)))
//    case ex                         => Http.fail(Response.fromHttpError(HttpError.InternalServerError(s"Error looking for an item: ${ex.getMessage}")))
//  }

  val program: ZIO[Any, Throwable, Nothing] = Server
    .start(8080, endpoints)
    .inject(Console.live, ZioQuillContext.dataSourceLayer, ItemService.layer, ItemRepository.layer)

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode
}
