package dev.xymox.zio.playground.migration

import dev.xymox.zio.playground.config.Configuration
import dev.xymox.zio.playground.logging.LoggingServices
import org.flywaydb.core.Flyway
import zio._

trait MigrationService {
  def clean: Task[Unit]
  def runBaseline: Task[Unit]
  def runMigrations: Task[Unit]
  def repairMigrations: Task[Unit]
}

object MigrationService {
  def clean: RIO[Has[MigrationService], Unit]            = ZIO.serviceWith[MigrationService](_.clean)
  def runBaseline: RIO[Has[MigrationService], Unit]      = ZIO.serviceWith[MigrationService](_.runBaseline)
  def runMigrations: RIO[Has[MigrationService], Unit]    = ZIO.serviceWith[MigrationService](_.runMigrations)
  def repairMigrations: RIO[Has[MigrationService], Unit] = ZIO.serviceWith[MigrationService](_.repairMigrations)

  val flywayFromConfig: RLayer[Has[MigrationConfig], Has[Flyway]] = {
    for {
      config <- ZIO.service[MigrationConfig]
      flyway <- ZIO.effect(Flyway.configure().dataSource(config.jdbcUrl, config.user, config.password).load())
    } yield flyway
  }.toLayer

  val serviceLayer: ZLayer[MigrationEnv, Throwable, Has[MigrationService]] =
    (MigrationServiceLive(_, _, _)).toLayer

  val live: TaskLayer[Has[MigrationService]] =
    Configuration.migrationConfigLive >>>
      LoggingServices.simpleLive ++ flywayFromConfig ++ ZEnv.live >>>
      serviceLayer
}
