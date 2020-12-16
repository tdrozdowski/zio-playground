package dev.xymox.zio.playground

import zio.{UIO, ZIO, ZLayer}
import zio.console._

object AccountObserver {

  trait Service {
    def processEvent(event: AccountEvent): UIO[Unit]
    def runCommand(): UIO[Unit]
  }

  def processEvent(event: AccountEvent): ZIO[AccountObserver, Nothing, Unit] =
    ZIO.accessM[AccountObserver](_.get.processEvent(event))

  def runCommand(): ZIO[AccountObserver, Nothing, Unit] =
    ZIO.accessM[AccountObserver](_.get.runCommand)

  val live: ZLayer[Console, Nothing, AccountObserver] =
    ZLayer.fromService[Console.Service, Service] { console =>
      new Service {
        def processEvent(event: AccountEvent): UIO[Unit] =
          for {
            _    <- console.putStrLn(s"Got $event")
            line <- console.getStrLn.orDie
            _    <- console.putStrLn(s"You entered: $line")
          } yield ()

        def runCommand(): UIO[Unit] =
          console.putStrLn("Done!")
      }
    }
}

case class AccountEvent(message: String)
