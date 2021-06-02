package dev.xymox.zio.playground.quill.repository

import io.getquill.context.ZioJdbc.QDataSource
import zio._
import zio.blocking.Blocking

trait ItemRepository extends Queries {
  def create(item: ItemRecord): Task[ItemRecord]
  def all: Task[Seq[ItemRecord]]
  def findById(id: Long): Task[ItemRecord]
}

object ItemRepository {

  def create(item: ItemRecord): RIO[Has[ItemRepository], ItemRecord] = ZIO.serviceWith[ItemRepository](_.create(item))
  def all: RIO[Has[ItemRepository], Seq[ItemRecord]]                 = ZIO.serviceWith[ItemRepository](_.all)
  def findById(id: Long): RIO[Has[ItemRepository], ItemRecord]       = ZIO.serviceWith[ItemRepository](_.findById(id))

  // live layer
  val layer: URLayer[QDataSource, Has[ItemRepository]] = (ItemRepositoryLive(_, _)).toLayer

}
