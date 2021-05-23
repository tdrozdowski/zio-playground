package dev.xymox.zio.playground.migration

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.{BaselineResult, CleanResult, MigrateResult}
import zio.{Task, ZIO}
import zio.blocking.Blocking
import zio.logging.Logger

import scala.jdk.CollectionConverters._

case class MigrationServiceLive(logger: Logger[String], flyway: Flyway, blocking: Blocking.Service) extends MigrationService {

  override def runMigrations: Task[Unit] =
    for {
      results <- blocking.blocking(ZIO.effect(flyway.migrate())) // handle errors?
      _       <- logger.info(
        s"Migration results: schema ${results.schemaName} started at version: ${results.initialSchemaVersion} and ended at version ${Option(results.targetSchemaVersion)
          .getOrElse(results.initialSchemaVersion)}."
      )
      _       <- logger.info(s"${migrationsRan(results)}")
    } yield ()

  private def migrationsRan(results: MigrateResult) = {
    if (results.migrations.asScala.nonEmpty) {
      s"Migrations ran: ${results.migrations.asScala.mkString("\n")}"
    } else {
      "No migrations ran."
    }
  }

  override def repairMigrations: Task[Unit] =
    for {
      results <- blocking.blocking(ZIO.effect(flyway.repair()))
      _       <- logger.info(
        s"Migration repair complete. Details - migrations removed: ${results.migrationsRemoved}, migrations aligned: ${results.migrationsAligned}, migrationed deleted: ${results.migrationsDeleted}.\n Repair actions: ${results.repairActions.asScala
          .mkString("\n")}\n Warnings: ${results.warnings.asScala.mkString("\n")}"
      )
    } yield ()

  override def runBaseline: Task[Unit] =
    for {
      results <- blocking.blocking(ZIO.effect(flyway.baseline()))
      _       <- logger.info(s"Completed baseline: ${baselineResultsMsg(results)} ")
    } yield ()

  override def clean: Task[Unit] =
    for {
      results <- blocking.blocking(ZIO.effect(flyway.clean()))
      _       <- logger.info(s"${cleanResultsMsg(results)}")
    } yield ()

  private def cleanResultsMsg(results: CleanResult) = {
    def warnings =
      if (results.warnings.asScala.nonEmpty) {
        s"Warnings: ${results.warnings.asScala.mkString("\n")}"
      } else {
        "No Warnings."
      }
    if (results.schemasCleaned.asScala.nonEmpty) {
      s"Schemas clean: ${results.schemasCleaned.asScala.mkString(",")}"
    } else if (results.schemasDropped.asScala.nonEmpty) {
      s"Schemas dropped: ${results.schemasDropped.asScala.mkString(",")}"
    } else {
      s"No schemas cleaned or dropped. $warnings"
    }
  }

  private def baselineResultsMsg(results: BaselineResult) = {
    def warningMessage =
      if (results.warnings.asScala.nonEmpty) {
        s"Warnings: ${results.warnings.asScala.mkString("\n")}"
      } else {
        "No warnings."
      }

    if (results.successfullyBaselined) {
      s"Baseline successful.  Baseline version: ${results.baselineVersion} - $warningMessage"
    } else {
      s"Baseline un-successful!  $warningMessage"
    }
  }

}
