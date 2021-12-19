package dev.xymox.zio.playground.zstack

import io.getquill.{PostgresZioJdbcContext, SnakeCase}

import java.time.Instant

package object repository {
  case class ItemRecord(id: Long = -1, name: String, description: String, unitPrice: Double, createdAt: Instant = Instant.now)
  case class InvoiceRecord(id: Long = -1, userId: Long, total: Double, paid: Boolean, createdAt: Instant)

  object MyContext extends PostgresZioJdbcContext(SnakeCase)

  case class NotFoundException(message: String, id: Long) extends Throwable
}
