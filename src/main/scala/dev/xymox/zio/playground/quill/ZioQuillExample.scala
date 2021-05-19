package dev.xymox.zio.playground.quill

import io.getquill.context.ZioJdbc.{QConnection, QDataSource}
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio._
import zio.blocking.Blocking
import zio.console._

import java.sql.{Timestamp, Types}
import java.time.Instant

object ZioQuillExample extends App {

  val ctx = new PostgresZioJdbcContext(SnakeCase)
  import ctx._

  implicit val itemSchemaMeta = schemaMeta[Item]("item")
  implicit val itemInsertMeta = insertMeta[Item](_.id)

  implicit val instantEncoder: Encoder[Instant] = encoder(Types.TIMESTAMP, (index, value, row) => row.setTimestamp(index, Timestamp.from(value)))
  implicit val instantDecoder: Decoder[Instant] = decoder((index, row) => { row.getTimestamp(index).toInstant })

  val zioConn: ZLayer[Blocking, Throwable, QConnection] =
    QDataSource.fromPrefix("zioQuillExample") >>> QDataSource.toConnection

  val anItem: Item = Item(id = -1, name = "Boomstick", description = "This...is my Boomstick!", unitPrice = 255.50, Instant.now)

  val itemsQuery             = quote(query[Item])
  def insertItem(item: Item) = quote(itemsQuery.insert(lift(anItem)))

  val insertAndQuery: RIO[QConnection, List[Item]] = ctx.transaction {
    for {
      _     <- ctx.run(insertItem(anItem))
      items <- ctx.run(itemsQuery)
    } yield items
  }

  val program: RIO[Console with QConnection, Unit] = for {
    _     <- putStrLn("Running zio-quill example...")
    items <- insertAndQuery
    _     <- putStrLn(s"Items ==> $items")
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.provideLayer(ZEnv.live ++ zioConn).exitCode
}

case class Item(id: Long, name: String, description: String, unitPrice: Double, createdAt: Instant = Instant.now)
