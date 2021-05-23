package dev.xymox.zio.playground.logging

import zio._
import zio.clock.Clock
import zio.console.Console
import zio.logging._

object LoggingServices {

  val simpleLogger: ZLayer[Console with Clock, Nothing, Logging] =
    Logging.console(
      logLevel = LogLevel.Info,
      format = LogFormat.ColoredLogFormat()
    ) >>> Logging.withRootLoggerName("zio-playground")

  val simpleLive: TaskLayer[Logging] = ZEnv.live >>> simpleLogger
}
