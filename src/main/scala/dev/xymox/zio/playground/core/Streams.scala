package dev.xymox.zio.playground.core

import zio._
import zio.console._
import zio.duration._
import zio.stream.{Sink, ZSink, ZStream}

object Streams extends App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    for {
      _ <- ZStream
        .iterate(0L)(_ + 1)
        .grouped(2000)
        .schedule(Schedule.spaced(50.millis))
        .tap(v => putStrLn(f"got: ${v.length}%5d ${v.head}%5d").orDie)
        .runDrain
    } yield ExitCode.success
  }
}

object StreamsAndSinks extends App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = for {
    stream <- ZIO.effectTotal(ZStream.fromIterable(1 to 1000000))
    sink   <- ZIO.effectTotal(ZSink.sum[Int])
    total  <- stream.run(sink)
    _      <- putStrLn(s"Sum of the first 1,000,000 Ints is: $total").orDie
  } yield ExitCode.success
}

object XformSinks extends App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = for {
    _        <- putStrLn("contramap example...").orDie
    stream   <- ZIO.effectTotal(ZStream.fromIterable(1 to 100))
    sink     <- ZIO.effectTotal(ZSink.collectAll[String].contramap[Int](_.toString + "_id"))
    results  <- stream.run(sink)
    _        <- putStrLn(s"Results = ${results.mkString(", ")}").orDie
    _        <- putStrLn("dimap example...").orDie
    sink2    <- ZIO.effectTotal(Sink.collectAll[String].dimap[Int, Chunk[String]](_.toString + "_id", _.take(10)))
    results2 <- stream.run(sink2)
    _        <- putStrLn(s"Results2 = ${results2.mkString(", ")}").orDie
  } yield ExitCode.success
}
