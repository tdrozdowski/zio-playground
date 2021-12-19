package dev.xymox.zio.playground.quill.repository

import io.getquill.context.qzio.ImplicitSyntax.{Implicit, ImplicitSyntaxOps}
import zio._

import javax.sql.DataSource

case class ItemRepositoryLive(dataSource: DataSource) extends ItemRepository {
  import MyContext._
  implicit val env = Implicit(Has(dataSource))

  override def create(item: ItemRecord): Task[ItemRecord] = transaction {
    for {
      _     <- run(ItemQueries.insertItem(item))
      items <- run(ItemQueries.itemsQuery)
    } yield items.headOption.getOrElse(throw new Exception("Insert failed!"))
  }.implicitly

  override def all: Task[Seq[ItemRecord]] = run(ItemQueries.itemsQuery).implicitly

  override def findById(id: Long): Task[ItemRecord] = {
    for {
      results <- run(ItemQueries.byId(id)).implicitly
      item    <- ZIO.fromOption(results.headOption).orElseFail(NotFoundException(s"Could not find item with id $id", id))
    } yield item
  }

}

object ItemQueries {

  import MyContext._

  // NOTE - if you put the type here you get a 'dynamic query' - which will never wind up working...
  implicit val itemSchemaMeta = schemaMeta[ItemRecord]("item")
  implicit val itemInsertMeta = insertMeta[ItemRecord](_.id)

  val itemsQuery                   = quote(query[ItemRecord])
  def byId(id: Long)               = quote(itemsQuery.filter(_.id == lift(id)))
  def insertItem(item: ItemRecord) = quote(itemsQuery.insert(lift(item)))
}
