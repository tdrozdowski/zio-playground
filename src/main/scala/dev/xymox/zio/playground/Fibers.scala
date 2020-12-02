package dev.xymox.zio.playground

import zio.UIO
import zio._
import zio.console._

object Fibers extends App {

  def fib(n: Long): UIO[Long] = UIO {
    if (n <= 1) UIO.succeed(n)
    else fib(n - 1).zipWithPar(fib(n - 2))(_ + _)
  }.flatten

  val fib100Fiber: UIO[Fiber[Nothing, Long]] =
    for {
      fiber <- fib(30).fork
    } yield fiber

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    for {
      fibFiber <- fib100Fiber
      fibResults <- fibFiber.join
      _ <- putStrLn(s"Results: ${fibResults.toLong}")
    } yield ExitCode.success
}
