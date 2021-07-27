package dev.xymox.zio.playground.zmx

import zio._
import zio.clock.Clock
import zio.console._
import zio.internal.Platform
import zio.zmx.diagnostics._

import java.io.IOException

object ZmxDiagnosticServerExample extends App {

  val program: ZIO[Console, IOException, Unit] =
    for {
      _ <- putStrLn("Waiting for input...")
      a <- getStrLn
      _ <- putStrLn(s"Thank you for $a")
    } yield ()

  val diagnosticsLayer: ZLayer[Clock with Console, Exception, Has[Diagnostics]] = Diagnostics.make("localhost", 1111)

  override def platform: Platform = super.platform.withSupervisor(ZMXSupervisor)

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    program.provideCustomLayer(diagnosticsLayer).exitCode

}

object ZmxDiagnosticClientExample extends App {
  val zmxConfig: ZMXConfig = ZMXConfig(host = "localhost", port = 1111, debug = false)
  val zmxClient: ZMXClient = new ZMXClient(zmxConfig)

  val program =
    for {
      _      <- putStrLn("Type command to send: ")
      rawCmd <- getStrLn
      cmd    <- if (Set("dump", "test") contains rawCmd) ZIO.succeed(rawCmd) else ZIO.fail(new RuntimeException("Invalid command"))
      resp   <- zmxClient.sendCommand(Chunk(cmd))
      _      <- putStrLn("Diagnotic response: ")
      _      <- putStrLn(resp)
    } yield ()

  override def platform: Platform = super.platform.withSupervisor(ZMXSupervisor)

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    program.exitCode
}
