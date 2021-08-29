package dev.xymox.zio.playground.zmx.server

import zhttp.http.{Http, Request, UResponse}
import zhttp.service.Server
import zio._
import zio.console._
import zio.magic._
import zio.zmx.prometheus.PrometheusClient
import zio.zmx.statsd.StatsdClient

import java.io.IOException

object InstrumentedServer extends App {

  val endpoints: Http[Has[StatsdClient] with Has[PrometheusClient], Throwable, Request, UResponse] =
    InstrumentedEndpoints.index +++ InstrumentedEndpoints.metrics +++ InstrumentedEndpoints.statsd

  val startServer: ZIO[Any, Throwable, Nothing] =
    Server.start(8081, endpoints).inject(StatsdClient.default, PrometheusClient.live)

  val program: ZIO[zio.ZEnv, IOException, Unit] =
    for {
      s <- startServer.fork
      p <- InstrumentedSample.program.fork
      _ <- putStrLn("Started metrics server and instrumented sample...[Press any key to kill this silly experiment]") *> getStrLn *> s.interrupt *> p.interrupt
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode
}
