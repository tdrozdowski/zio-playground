package dev.xymox.zio.playground.tapir

import dev.xymox.zio.playground.zstack.service.item.ItemService
import zhttp.http.{Http, Request, Response}
import zhttp.service.Server
import zio._
import zio.magic._

object TapirItemServer extends App {

  val endpoints: Http[Any, Throwable, Request, Response[Any, Throwable]] = TapirItemEndpoints.zhttpApp +++ ItemDocs.redocApp

  val program: ZIO[Any, Throwable, Nothing] = Server
    .start(8080, endpoints)
    .inject(ItemService.layer)

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode
}
