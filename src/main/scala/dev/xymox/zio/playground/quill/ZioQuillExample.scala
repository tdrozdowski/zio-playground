package dev.xymox.zio.playground.quill

import io.getquill.context.ZioJdbc.DataSourceLayer
import io.getquill.context.qzio.ImplicitSyntax.Implicit
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import zio._
import zio.console._

import java.sql.{Timestamp, Types}
import java.time.Instant
import javax.sql.DataSource

object ZioQuillExample extends App {
  // new ZIO JDBC Context for Quill!
  val ctx = new PostgresZioJdbcContext(SnakeCase)
  import ctx._

  // some Meta classes to help Quill
  implicit val itemSchemaMeta = schemaMeta[ItemRow]("item")
  implicit val itemInsertMeta = insertMeta[ItemRow](_.id)

  // simple layer providing a connection for the effect; this is pulled from a HikariCP
  // NOTE - prefix is the HOCON prefix in the application.conf to look for
  val zioDS: TaskLayer[Has[DataSource]] = DataSourceLayer.fromPrefix("zioQuillExample")

  // needed to implicitly provide the data source to the effect
  implicit val env: Implicit[TaskLayer[Has[DataSource]]] = Implicit(zioDS)
  // an item to insert...
  val anItem: ItemRow = ItemRow(id = -1, name = "Boomstick", description = "This...is my Boomstick!", unitPrice = 255.50, Instant.now)

  // some Quill queries
  val itemsQuery                = quote(query[ItemRow])
  def insertItem(item: ItemRow) = quote(itemsQuery.insert(lift(anItem)))

  // the transactional use of the context (this belongs in a DAO/Repository ZIO Service module)
  val insertAndQuery: RIO[Has[DataSource], List[ItemRow]] = ctx.transaction {
    for {
      _     <- ctx.run(insertItem(anItem))
      items <- ctx.run(itemsQuery)
    } yield items
  }

  // our program!
  val program: RIO[Console with Has[DataSource], Unit] = for {
    _     <- putStrLn("Running zio-quill example...")
    items <- insertAndQuery
    _     <- putStrLn(s"Items ==> $items")
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.provideLayer(ZEnv.live ++ zioDS).exitCode
}

case class ItemRow(id: Long, name: String, description: String, unitPrice: Double, createdAt: Instant = Instant.now)
