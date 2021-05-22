package dev.xymox.zio.playground

import zio.{RIO, Task, UIO, ZIO, ZLayer}
import zio.console._

object AccountObserver {

  trait Service {
    def processEvent(event: AccountEvent): Task[Unit]
    def runCommand(): Task[Unit]
  }

  def processEvent(event: AccountEvent): RIO[AccountObserver, Unit] =
    ZIO.accessM[AccountObserver](_.get.processEvent(event))

  def runCommand(): RIO[AccountObserver, Unit] =
    ZIO.accessM[AccountObserver](_.get.runCommand)

  val live: ZLayer[Console, Nothing, AccountObserver] =
    ZLayer.fromService[Console.Service, Service] { console =>
      new Service {
        def processEvent(event: AccountEvent): Task[Unit] =
          for {
            _    <- console.putStrLn(s"Got $event")
            line <- console.getStrLn.orDie
            _    <- console.putStrLn(s"You entered: $line")
          } yield ()

        def runCommand(): Task[Unit] =
          console.putStrLn("Done!")
      }
    }
}

case class AccountEvent(message: String)
