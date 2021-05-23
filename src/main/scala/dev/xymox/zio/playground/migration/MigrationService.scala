package dev.xymox.zio.playground.migration

import dev.xymox.zio.playground.config.Configuration
import dev.xymox.zio.playground.logging.LoggingServices
import org.flywaydb.core.Flyway
import zio._
import zio.blocking.Blocking
import zio.logging.Logger

trait MigrationService {
  def clean: Task[Unit]
  def runBaseline: Task[Unit]
  def runMigrations: Task[Unit]
  def repairMigrations: Task[Unit]
}

object MigrationService {
  def clean: RIO[Migration, Unit]            = ZIO.accessM(_.get.clean)
  def runBaseline: RIO[Migration, Unit]      = ZIO.accessM(_.get.runBaseline)
  def runMigrations: RIO[Migration, Unit]    = ZIO.accessM(_.get.runMigrations)
  def repairMigrations: RIO[Migration, Unit] = ZIO.accessM(_.get.repairMigrations)

  val flywayFromConfig: RLayer[Has[MigrationConfig], Has[Flyway]] = {
    for {
      config <- ZIO.service[MigrationConfig]
      flyway <- ZIO.effect(Flyway.configure().dataSource(config.jdbcUrl, config.user, config.password).load())
    } yield flyway
  }.toLayer

  /** Helper to extract dependencies from ZIO environment for the service
    */
  private val extractDependencies: URIO[MigrationDep, MigrationServices] =
    ZIO.services[Logger[String], Flyway, Blocking.Service]

  val serviceLayer: ZLayer[MigrationEnv, Throwable, Has[MigrationService]] = {
    for {
      (logger, flyway, blocking) <- extractDependencies
    } yield DefaultMigrationService(logger, flyway, blocking)
  }.toLayer

  val live: TaskLayer[Has[MigrationService]] =
    Configuration.migrationConfigLive >>>
      LoggingServices.simpleLive ++ flywayFromConfig ++ ZEnv.live >>>
      serviceLayer
}
