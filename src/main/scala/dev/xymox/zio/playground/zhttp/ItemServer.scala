package dev.xymox.zio.playground.zhttp

import dev.xymox.zio.playground.quill.{ItemService, ZioQuillContext}
import dev.xymox.zio.playground.quill.repository.{ItemRepository, NotFoundException}
import zhttp.http._
import zhttp.service.Server
import zio._
import zio.console._
import zio.magic._

object ItemServer extends App {

  val endpoints: Http[Has[ItemService], HttpError, Request, UResponse] = ItemEndpoints.item

  val program: ZIO[Any, Throwable, Nothing] = Server
    .start(8080, endpoints)
    .inject(Console.live, ZioQuillContext.dataSourceLayer, ItemService.layer, ItemRepository.layer)

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode
}
