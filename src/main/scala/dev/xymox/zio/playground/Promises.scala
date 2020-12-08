package dev.xymox.zio.playground

import zio._
import zio.clock._
import zio.console._
import zio.duration._

object Promises extends App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    for {
      promise <- Promise.make[Nothing, String]
      sendHelloWorld = (IO.succeed("hello world") <* sleep(1.second)).flatMap(promise.succeed)
      getAndPrint = promise.await.flatMap(putStrLn(_))
      fiberA <- sendHelloWorld.fork
      fiberB <- getAndPrint.fork
      _ <- (fiberA zip fiberB).join
    } yield ExitCode.success
}
