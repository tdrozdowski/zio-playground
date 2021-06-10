package dev.xymox.zio.playground.core

import zio.clock.{sleep, Clock}
import zio.console.{putStrLn, Console}
import zio.{App, ExitCode, Promise, URIO, ZIO}

import java.io.IOException

import zio.duration._

object Promises extends App {

  val program: ZIO[Console with Clock, IOException, Unit] =
    for {
      promise       <- Promise.make[Nothing, String]
      sendHelloWorld = sleep(1.second).as("hello world").flatMap(promise.succeed)
      getAndPrint    = promise.await.flatMap(putStrLn(_))
      fiberA        <- sendHelloWorld.fork
      fiberB        <- getAndPrint.fork
      _             <- (fiberA zip fiberB).join
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode
}
