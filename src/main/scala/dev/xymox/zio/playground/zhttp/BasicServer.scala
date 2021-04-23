package dev.xymox.zio.playground.zhttp

import zhttp.http._
import zhttp.service.Server
import zio._

object BasicServer extends App {
  val composedApp                                                = HelloWorldApp.app <> HelloWorldApp.jsonApp
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = Server.start(8080, composedApp).exitCode
}
