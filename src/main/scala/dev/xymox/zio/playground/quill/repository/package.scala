package dev.xymox.zio.playground.quill

import io.getquill.context.ZioJdbc.{QConnection, QDataSource}
import io.getquill.context.jdbc.JdbcRunContext
import io.getquill.{NamingStrategy, PostgresZioJdbcContext, SnakeCase}
import zio.blocking.Blocking
import zio.{Has, ZIO}

import java.io.Closeable
import java.sql.{Connection, Timestamp, Types}
import java.time.Instant
import javax.sql.DataSource

package object repository {
  case class ItemRecord(id: Long, name: String, description: String, unitPrice: Double, createdAt: Instant = Instant.now)

  // Until Quill is updated with this...
  // taken from Discord chat on #zio-quill
  implicit class RunExtDs[E <: Throwable, T](run: ZIO[Has[Connection] with Blocking, E, T]) {

    def asDao: ZIO[Has[DataSource with Closeable] with Blocking, Throwable, T] =
      for {
        ds     <- ZIO.environment[Has[DataSource with Closeable] with Blocking]
        conn   <- QDataSource.toConnection.build.useNow.provide(ds)
        result <- run.provide(conn)
      } yield result

    def withConnection: ZIO[QConnection, Throwable, T] =
      for {
        conn   <- ZIO.environment[QConnection]
        result <- run.provide(conn)
      } yield result
  }

//  implicit class TransactionExt[E <: Throwable, T](f: ZIO[QConnection, Throwable, T]) {
//
//    def asDao: ZIO[Has[DataSource with Closeable] with Blocking, Throwable, T] =
//      for {
//        ds     <- ZIO.environment[Has[DataSource with Closeable] with Blocking]
//        conn   <- QDataSource.toConnection.build.useNow.provide(ds)
//        result <- f.provide(conn)
//      } yield result
//
//    def provideConnection(ds: QDataSource): ZIO[QConnection, Throwable, T] =
//      for {
//        conn   <- QDataSource.toConnection.build.useNow.provide(ds)
//        result <- f.provide(conn)
//      } yield result
//  }

  class MyZioContext[N <: NamingStrategy](override val naming: N) extends PostgresZioJdbcContext(naming)

  //noinspection DuplicatedCode
  trait InstantEncoding {
    val ctx: MyZioContext[SnakeCase]

    import ctx._

    implicit val instantDecoder: Decoder[Instant] = decoder((index, row) => { row.getTimestamp(index).toInstant })
    implicit val instantEncoder: Encoder[Instant] = encoder(Types.TIMESTAMP, (idx, value, row) => row.setTimestamp(idx, Timestamp.from(value)))
  }
}
