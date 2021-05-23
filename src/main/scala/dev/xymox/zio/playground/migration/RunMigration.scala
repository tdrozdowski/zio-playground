package dev.xymox.zio.playground.migration

import zio._
import zio.console._

object RunMigration extends App {

  val env: TaskLayer[Console with Has[MigrationService]] = ZEnv.live ++ MigrationService.live

  val program: RIO[Console with Migration, Unit] =
    for {
      _ <- putStrLn("Starting migrations...")
      _ <- MigrationService.clean *> MigrationService.runMigrations
      _ <- putStr("Completed.")
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.provideCustomLayer(env).exitCode
}
