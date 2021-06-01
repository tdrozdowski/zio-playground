package dev.xymox.zio.playground.quill

import io.getquill.context.ZioJdbc.{QConnection, QDataSource}
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio._
import zio.blocking.Blocking
import zio.console._

import java.sql.{Timestamp, Types}
import java.time.Instant

object ZioQuillExample extends App {
  // new ZIO JDBC Context for Quill!
  val ctx = new PostgresZioJdbcContext(SnakeCase)
  import ctx._

  // some Meta classes to help Quill
  implicit val itemSchemaMeta = schemaMeta[ItemRow]("item")
  implicit val itemInsertMeta = insertMeta[ItemRow](_.id)

  // some Encoders for Instant so Quill knows what to do with an Instant
  implicit val instantEncoder: Encoder[Instant] = encoder(Types.TIMESTAMP, (index, value, row) => row.setTimestamp(index, Timestamp.from(value)))
  implicit val instantDecoder: Decoder[Instant] = decoder((index, row) => { row.getTimestamp(index).toInstant })

  // simple layer providing a connection for the effect; this is pulled from a HikariCP
  // NOTE - prefix is the HOCON prefix in the application.conf to look for
  val zioConn: ZLayer[Blocking, Throwable, QConnection] =
    QDataSource.fromPrefix("zioQuillExample") >>> QDataSource.toConnection

  // an item to insert...
  val anItem: ItemRow = ItemRow(id = -1, name = "Boomstick", description = "This...is my Boomstick!", unitPrice = 255.50, Instant.now)

  // some Quill queries
  val itemsQuery                = quote(query[ItemRow])
  def insertItem(item: ItemRow) = quote(itemsQuery.insert(lift(anItem)))

  // the transactional use of the context (this belongs in a DAO/Repository ZIO Service module)
  val insertAndQuery: RIO[QConnection, List[ItemRow]] = ctx.transaction {
    for {
      _     <- ctx.run(insertItem(anItem))
      items <- ctx.run(itemsQuery)
    } yield items
  }

  // our program!
  val program: RIO[Console with QConnection, Unit] = for {
    _     <- putStrLn("Running zio-quill example...")
    items <- insertAndQuery
    _     <- putStrLn(s"Items ==> $items")
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.provideLayer(ZEnv.live ++ zioConn).exitCode
}

case class ItemRow(id: Long, name: String, description: String, unitPrice: Double, createdAt: Instant = Instant.now)
