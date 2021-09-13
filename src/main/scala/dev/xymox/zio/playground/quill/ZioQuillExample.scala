package dev.xymox.zio.playground.quill

import dev.xymox.zio.playground.quill.ZioQuillContext.QDataSource
import io.getquill.context.ZioJdbc.{DataSourceLayer, QConnection, QDataSource, QuillZioExt}
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio._
import zio.blocking.Blocking
import zio.console._

import java.io.Closeable
import java.sql.{Connection, Timestamp, Types}
import java.time.Instant
import javax.sql.DataSource

object ZioQuillExample extends App {
  // new ZIO JDBC Context for Quill!
  val ctx = new PostgresZioJdbcContext(SnakeCase)
  import ctx._

  // some Meta classes to help Quill
  implicit val itemSchemaMeta = schemaMeta[ItemRow]("item")
  implicit val itemInsertMeta = insertMeta[ItemRow](_.id)

  // some Encoders for Instant so Quill knows what to do with an Instant
  implicit val instantEncoder: Encoder[Instant] = encoder(Types.TIMESTAMP, (index, value, row) => row.setTimestamp(index, Timestamp.from(value)))
  implicit val instantDecoder: Decoder[Instant] = decoder((index, row, _) => { row.getTimestamp(index).toInstant })

  // simple layer providing a connection for the effect; this is pulled from a HikariCP
  // NOTE - prefix is the HOCON prefix in the application.conf to look for
  val zioDS: TaskLayer[Has[DataSource with Closeable]] = DataSourceLayer.fromPrefix("zioQuillExample")

  // an item to insert...
  val anItem: ItemRow = ItemRow(id = -1, name = "Boomstick", description = "This...is my Boomstick!", unitPrice = 255.50, Instant.now)

  // some Quill queries
  val itemsQuery                = quote(query[ItemRow])
  def insertItem(item: ItemRow) = quote(itemsQuery.insert(lift(anItem)))

  // the transactional use of the context (this belongs in a DAO/Repository ZIO Service module)
  val insertAndQuery: RIO[Has[Connection], List[ItemRow]] = ctx.transaction {
    for {
      _     <- ctx.run(insertItem(anItem))
      items <- ctx.run(itemsQuery)
    } yield items
  }

  // our program!
  val program: RIO[Console with QDataSource, Unit] = for {
    _     <- putStrLn("Running zio-quill example...")
    items <- insertAndQuery.onDataSource.provideLayer(zioDS)
    _     <- putStrLn(s"Items ==> $items")
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.provideLayer(ZEnv.live ++ zioDS).exitCode
}

case class ItemRow(id: Long, name: String, description: String, unitPrice: Double, createdAt: Instant = Instant.now)
