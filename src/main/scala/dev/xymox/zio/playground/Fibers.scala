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
      fiber <- fib(20).fork
    } yield fiber

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    for {
      fibFiber <- fib100Fiber
      fibResults <- fibFiber.join
      _ <- putStrLn(s"Results: ${fibResults.toLong}")
    } yield ExitCode.success
}

object FiberRefs extends App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    for {
      _ <- putStrLn("Basic FiberRef example....")
      fiberRef <- FiberRef.make[Int](0)
      _ <- fiberRef.set(10)
      results <- fiberRef.get
      _ <- putStrLn(s"Results -> $results")
      _ <- putStrLn("Local FiberRef example...")
      correlationid <- FiberRef.make[String]("")
      v1 <- correlationid.locally("my-correlation-id")(correlationid.get)
      v2 <- correlationid.get
      _ <- putStrLn(s"CorrelationId: v1: '$v1' vs v2: '$v2'")
    } yield ExitCode.success
}
