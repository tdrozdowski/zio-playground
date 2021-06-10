package dev.xymox.zio.playground.core

import zio.stream.ZStream
import zio.{console, App, ExitCode, Hub, Promise, URIO, ZEnv, ZIO}

object ZHubExample extends App {

  def run(args: List[String]): URIO[ZEnv, ExitCode] =
    for {
      promise   <- Promise.make[Nothing, Unit]
      hub       <- Hub.bounded[String](2)
      stream     = ZStream.managed(hub.subscribe).flatMap { queue =>
        ZStream.fromEffect(promise.succeed(())) *>
          ZStream.fromQueue(queue)
      }
      fiber     <- stream.take(2).runCollect.fork
      _         <- promise.await
      _         <- hub.publish("Hello")
      _         <- hub.publish("World")
      collected <- fiber.join
      _         <- ZIO.foreach(collected)(console.putStrLn(_)).orDie
    } yield ExitCode.success
}
