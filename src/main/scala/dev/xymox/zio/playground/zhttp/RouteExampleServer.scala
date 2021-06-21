package dev.xymox.zio.playground.zhttp

import zhttp.service.Server
import zio._

object RouteExampleServer extends App {

  val program: ZIO[Has[_], Throwable, Nothing] = Server
    .start(8080, InvoiceEndpoints.invoiceRoutes)

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode
}
