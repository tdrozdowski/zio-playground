package dev.xymox.zio.playground.quill.repository

import io.getquill.SnakeCase
import io.getquill.context.ZioJdbc.QuillZioExt
import zio._
import zio.blocking.Blocking

import java.io.Closeable
import javax.sql.DataSource

case class ItemRepositoryLive(dataSource: DataSource with Closeable, blocking: Blocking.Service) extends ItemRepository with Queries {
  val dataSourceLayer: Has[DataSource with Closeable] with Has[Blocking.Service] = Has.allOf[DataSource with Closeable, Blocking.Service](dataSource, blocking)

  val ctx: MyZioContext[SnakeCase] = new MyZioContext[SnakeCase](SnakeCase)

  import ctx._

  override def create(item: ItemRecord): Task[ItemRecord] = ctx.transaction {
    for {
      _     <- ctx.run(insertItem(item))
      items <- ctx.run(itemsQuery)
    } yield items.headOption.getOrElse(throw new Exception("Insert failed!"))
  }.dependOnDataSource().provide(dataSourceLayer)

  override def all: Task[Seq[ItemRecord]] = ctx.run(itemsQuery).dependOnDataSource().provide(dataSourceLayer)

  override def findById(id: Long): Task[ItemRecord] = {
    for {
      results <- ctx.run(byId(id)).dependOnDataSource().provide(dataSourceLayer)
      item    <- ZIO.fromOption(results.headOption).orElseFail(NotFoundException(s"Could not find item with id $id", id))
    } yield item
  }

}

trait Queries {
  val ctx: MyZioContext[_]

  import ctx._

  // NOTE - if you put the type here you get a 'dynamic query' - which will never wind up working...
  implicit val itemSchemaMeta = schemaMeta[ItemRecord]("item")
  implicit val itemInsertMeta = insertMeta[ItemRecord](_.id)

  val itemsQuery                   = quote(query[ItemRecord])
  def byId(id: Long)               = quote(itemsQuery.filter(_.id == lift(id)))
  def insertItem(item: ItemRecord) = quote(itemsQuery.insert(lift(item)))
}
