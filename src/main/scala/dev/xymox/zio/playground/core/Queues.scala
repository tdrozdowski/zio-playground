package dev.xymox.zio.playground.core

import zio._
import zio.clock._
import zio.console._
import zio.duration._

import java.util.concurrent.TimeUnit

object Queues extends App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    for {
      queue <- Queue.bounded[Int](1)
      _     <- queue.offer(1)
      f     <- queue.offer(1).fork
      n     <- queue.take
      v     <- f.join
      _     <- putStrLn(s"Value: $n fiber done: $v").orDie
    } yield ExitCode.success
}

object TransformingQueues extends App {
  val currentTimeMillis = currentTime(TimeUnit.MILLISECONDS)

  val annotatedOut = for {
    queue <- Queue.bounded[String](10)
    mapped = queue.mapM { el => currentTimeMillis.map((_, el)) }
  } yield mapped

  val annotatedIn = for {
    queue <- Queue.bounded[(Long, String)](3)
    mapped = queue.contramapM { el: String => currentTimeMillis.map((_, el)) }
  } yield mapped

  val timeQueued = for {
    queue            <- Queue.bounded[(Long, String)](3)
    enqueueTimeStamps = queue.contramapM { el: String => currentTimeMillis.map((_, el)) }
    durations         = enqueueTimeStamps.mapM { case (enqueueTs, el) => currentTimeMillis.map(dequeueTs => ((dequeueTs - enqueueTs), el)) }
  } yield durations

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = for {
    queue <- annotatedOut
    _     <- queue.offer("This is a test")
    v     <- queue.take
    _     <- putStrLn(s"Value: $v").orDie
    _     <- putStrLn("Time in queue example...").orDie
    q2    <- timeQueued
    _     <- q2.offer("Another test")
    v2    <- q2.take <* sleep(1.second)
    _     <- putStrLn(s"Value: ${v2._2} was in the queue for ${v2._1} millis!").orDie
  } yield ExitCode.success
}
