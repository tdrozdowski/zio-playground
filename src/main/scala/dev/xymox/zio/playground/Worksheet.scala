package dev.xymox.zio.playground

import zio._

object Worksheet extends App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    for {
      r <- UIO.effectTotal(1 / 0)
    } yield ExitCode.success
  }
}
