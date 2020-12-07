package dev.xymox.zio.playground

import zio._
import zio.console._
import zio.duration._
import zio.stream.{ZSink, ZStream}

object Streams extends App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    for {
      _ <- ZStream
        .iterate(0L)(_ + 1)
        .grouped(2000)
        .schedule(Schedule.spaced(50.millis))
        .tap(v => putStrLn(f"got: ${v.length}%5d ${v.head}%5d"))
        .runDrain
    } yield ExitCode.success
  }
}

object StreamsAndSinks extends App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = for {
    stream <- ZIO.effectTotal(ZStream.fromIterable(1 to 1000000))
    sink <- ZIO.effectTotal(ZSink.sum[Int])
    total <- stream.run(sink)
    _ <- putStrLn(s"Sum of the first 1,000,000 Ints is: $total")
  } yield ExitCode.success
}
