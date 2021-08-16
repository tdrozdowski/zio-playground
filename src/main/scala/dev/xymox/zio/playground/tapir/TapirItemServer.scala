package dev.xymox.zio.playground.tapir

import dev.xymox.zio.playground.quill.ZioQuillContext
import dev.xymox.zio.playground.zstack.repository.ItemRepository
import dev.xymox.zio.playground.zstack.service.item.ItemService
import zhttp.http.{Http, Request, Response}
import zhttp.service.Server
import zio._
import zio.console.Console
import zio.magic._

object TapirItemServer extends App {

  val endpoints: Http[Has[ItemService], Throwable, Request, Response[Has[ItemService], Throwable]] = ItemDocs.redocApp +++ TapirItemEndpoints.zhttpApp

  val program: ZIO[Any, Throwable, Nothing] = Server
    .start(8080, endpoints)
    .inject(Console.live, ZioQuillContext.dataSourceLayer, ItemRepository.layer, ItemService.layer)

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode
}
