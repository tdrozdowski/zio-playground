package dev.xymox.zio.playground.quill.repository

import io.getquill.SnakeCase
import zio._
import zio.blocking.Blocking

import java.io.Closeable
import javax.sql.DataSource

case class ItemRepositoryLive(dataSource: DataSource with Closeable, blocking: Blocking.Service) extends ItemRepository with Queries {
  val dataSourceLayer: Has[DataSource with Closeable] with Has[Blocking.Service] = Has.allOf[DataSource with Closeable, Blocking.Service](dataSource, blocking)

  val ctx = new MyZioContext(SnakeCase)
  import ctx._

  implicit val itemSchemaMeta: ctx.SchemaMeta[ItemRecord] = schemaMeta[ItemRecord]("item")
  implicit val itemInsertMeta: ctx.InsertMeta[ItemRecord] = insertMeta[ItemRecord](_.id)

  override def create(item: ItemRecord): Task[ItemRecord] = ctx.transaction {
    for {
      _     <- ctx.run(insertItem(item)).withConnection
      items <- ctx.run(itemsQuery).withConnection
    } yield items.headOption.getOrElse(throw new Exception("Insert failed!"))
  }.asDao.provide(dataSourceLayer)

  override def all: Task[Seq[ItemRecord]] = ctx.run(itemsQuery).asDao.provide(dataSourceLayer)

  override def findById(id: Long): Task[ItemRecord] =
    ctx.run(byId(id)).map(_.headOption.getOrElse(throw new Exception(s"Can't find for id $id"))).asDao.provide(dataSourceLayer)

}

trait Queries extends InstantEncoding {
  val ctx: MyZioContext[SnakeCase]

  import ctx._

  val itemsQuery                   = quote(query[ItemRecord])
  def byId(id: Long)               = quote(itemsQuery.filter(_.id == lift(id)))
  def insertItem(item: ItemRecord) = quote(itemsQuery.insert(lift(item)))
}
