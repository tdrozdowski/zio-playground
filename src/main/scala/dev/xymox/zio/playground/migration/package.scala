package dev.xymox.zio.playground

import org.flywaydb.core.Flyway
import zio.Has
import zio.config._
import zio.config.magnolia.DeriveConfigDescriptor._
import zio.logging.Logger

package object migration {
  type Migration         = Has[MigrationService]
  type MigrationDep      = Has[Logger[String]] with Has[Flyway]
  type MigrationEnv      = Has[Logger[String]] with Has[Flyway] with Has[MigrationConfig]
  type MigrationServices = (Logger[String], Flyway)

  implicit val migrationConfigDescriptor: ConfigDescriptor[MigrationConfig] = descriptor[MigrationConfig].mapKey(toKebabCase)
  // setup zio-config stuff for this (and others)
  case class MigrationConfig(jdbcUrl: String, user: String, password: String)
}
