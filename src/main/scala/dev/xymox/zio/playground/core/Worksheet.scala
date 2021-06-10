package dev.xymox.zio.playground.core

import zio.{App, ExitCode, UIO, URIO}

object Worksheet extends App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    for {
      r <- UIO.effectTotal(1 / 0)
    } yield ExitCode.success
  }
}
