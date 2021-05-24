package dev.xymox.zio.playground.quill.repository

import io.getquill.SnakeCase
import io.getquill.context.ZioJdbc.QDataSource
import zio.Task

case class ItemRepositoryLive(dataSourceLayer: QDataSource) extends ItemRepository with Queries {
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

  override def findById(id: Int): Task[ItemRecord] =
    ctx.run(byId(id)).map(_.headOption.getOrElse(throw new Exception(s"Can't find for id $id"))).asDao.provide(dataSourceLayer)

}

trait Queries extends InstantEncoding {
  val ctx: MyZioContext[SnakeCase]

  import ctx._

  val itemsQuery                   = quote(query[ItemRecord])
  def byId(id: Int)                = quote(itemsQuery.filter(_.id == lift(id)))
  def insertItem(item: ItemRecord) = quote(itemsQuery.insert(lift(item)))
}
