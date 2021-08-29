package dev.xymox.zio.playground.zstack

import io.getquill.context.jdbc.JdbcRunContext
import io.getquill.{NamingStrategy, PostgresZioJdbcContext, SnakeCase}

import java.sql.{Timestamp, Types}
import java.time.Instant

package object repository {
  case class ItemRecord(id: Long = -1, name: String, description: String, unitPrice: Double, createdAt: Instant = Instant.now)
  case class InvoiceRecord(id: Long = -1, userId: Long, total: Double, paid: Boolean, createdAt: Instant)

  object MyContext extends PostgresZioJdbcContext(SnakeCase) with InstantEncoding

  //noinspection DuplicatedCode
  trait InstantEncoding { this: JdbcRunContext[_, _] =>
    implicit val instantDecoder: Decoder[Instant] = decoder((index, row) => { row.getTimestamp(index).toInstant })
    implicit val instantEncoder: Encoder[Instant] = encoder(Types.TIMESTAMP, (idx, value, row) => row.setTimestamp(idx, Timestamp.from(value)))
  }

  case class NotFoundException(message: String, id: Long) extends Throwable
}
