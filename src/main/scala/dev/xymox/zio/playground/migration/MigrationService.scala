package dev.xymox.zio.playground.migration

import dev.xymox.zio.playground.config.Configuration
import dev.xymox.zio.playground.logging.LoggingServices
import org.flywaydb.core.Flyway
import zio._
import zio.blocking.Blocking
import zio.logging.Logger

import scala.jdk.CollectionConverters._

trait MigrationService {
  def runMigrations: Task[Unit]
  def repairMigrations: Task[Unit]
}

object MigrationService {

  def runMigrations: RIO[Migration, Unit]    = ZIO.accessM(_.get.runMigrations)
  def repairMigrations: RIO[Migration, Unit] = ZIO.accessM(_.get.repairMigrations)

  // Should it be wrapped in a Blocking or ZManaged?
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
    } yield new MigrationService {
      override def runMigrations: Task[Unit] =
        for {
          results <- blocking.blocking(ZIO.effect(flyway.migrate())) // handle errors?
          _       <- logger.info(
            s"Migration results: schema ${results.schemaName} started at version: ${results.initialSchemaVersion} and ended at version ${results.targetSchemaVersion}."
          )
          _       <- logger.info(s"Migrations ran: ${results.migrations.asScala.mkString("\n")}")
        } yield ()

      override def repairMigrations: Task[Unit] =
        for {
          results <- blocking.blocking(ZIO.effect(flyway.repair()))
          _       <- logger.info(
            s"Migration repair complete. Details - migrations removed: ${results.migrationsRemoved}, migrations aligned: ${results.migrationsAligned}, migrationed deleted: ${results.migrationsDeleted}.\n Repair actions: ${results.repairActions.asScala
              .mkString("\n")}\n Warnings: ${results.warnings.asScala.mkString("\n")}"
          )
        } yield ()
    }
  }.toLayer

  val live: TaskLayer[Has[MigrationService]] =
    Configuration.migrationConfigLive >>> LoggingServices.simpleLive ++ flywayFromConfig ++ ZEnv.live >>> serviceLayer
}
