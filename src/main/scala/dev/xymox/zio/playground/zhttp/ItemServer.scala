package dev.xymox.zio.playground.zhttp

import dev.xymox.zio.playground.quill.{ItemService, ZioQuillContext}
import dev.xymox.zio.playground.quill.repository.{ItemRepository, NotFoundException}
import dev.xymox.zio.playground.zhttp.auth.AuthenticationApp
import zhttp.http._
import zhttp.service.Server
import zio._
import zio.console._
import zio.magic._

object ItemServer extends App {

  val endpoints: Http[Has[ItemService] with Console, HttpError, Request, Response[Has[ItemService] with Console, HttpError]] =
    AuthenticationApp.login +++ AuthenticationApp.authenticate(HttpApp.forbidden("None shall pass."), ItemEndpoints.item)

  val program: ZIO[Any, Throwable, Nothing] = Server
    .start(8080, endpoints)
    .inject(Console.live, ZioQuillContext.dataSourceLayer, ItemService.layer, ItemRepository.layer)

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode
}
