package dev.xymox.zio.playground.core

import zio.console._
import zio.{UIO, ZEnv, _}

import java.io.IOException

object Fibers extends App {

  def fib(n: Long): UIO[Long] = UIO {
    if (n <= 1) UIO.succeed(n)
    else fib(n - 1).zipWithPar(fib(n - 2))(_ + _)
  }.flatten

  val fib100Fiber: Task[Fiber[Nothing, Long]] =
    for {
      fiber <- fib(20).fork
    } yield fiber

  val program: ZIO[Console, Throwable, Unit] =
    for {
      fibFiber   <- fib100Fiber
      fibResults <- fibFiber.join
      _          <- putStrLn(s"Results: ${fibResults.toLong}").orDie
    } yield ()

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    program.exitCode
}

object FiberRefs extends App {

  val program: ZIO[Console, IOException, Unit] =
    for {
      _             <- putStrLn("Basic FiberRef example....").orDie
      fiberRef      <- FiberRef.make[Int](0)
      _             <- fiberRef.set(10)
      results       <- fiberRef.get
      _             <- putStrLn(s"Results -> $results").orDie
      _             <- putStrLn("Local FiberRef example...").orDie
      correlationid <- FiberRef.make[String]("")
      v1            <- correlationid.locally("my-correlation-id")(correlationid.get)
      v2            <- correlationid.get
      _             <- putStrLn(s"CorrelationId: v1: '$v1' vs v2: '$v2'").orDie
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode

}
