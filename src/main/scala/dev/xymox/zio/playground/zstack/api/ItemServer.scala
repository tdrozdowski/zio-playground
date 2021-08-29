package dev.xymox.zio.playground.zstack.api

import dev.xymox.zio.playground.quill.ZioQuillContext
import dev.xymox.zio.playground.zhttp.auth.AuthenticationApp
import dev.xymox.zio.playground.zstack.repository.ItemRepository
import dev.xymox.zio.playground.zstack.service.item.ItemService
import zhttp.http._
import zhttp.service.Server
import zio._
import zio.console._
import zio.magic._
import zio.zmx.prometheus.PrometheusClient

object ItemServer extends App {

  val endpoints: Http[Has[PrometheusClient] with Has[ItemService] with Console, HttpError, Request, Response[Has[ItemService] with Console, HttpError]] =
    MetricsEndpoints.metrics +++ AuthenticationApp.login +++ CORS(
      AuthenticationApp.authenticate(HttpApp.forbidden("None shall pass."), ItemEndpoints.item),
      config = CORSConfig(anyOrigin = true)
    )

  val program: ZIO[Any, Throwable, Nothing] = Server
    .start(8080, endpoints)
    .inject(Console.live, ZioQuillContext.dataSourceLayer, ItemService.layer, ItemRepository.layer, PrometheusClient.live)

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode
}
