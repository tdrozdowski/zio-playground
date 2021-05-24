package dev.xymox.zio.playground.quill.repository

import io.getquill.context.ZioJdbc.QDataSource
import zio._

trait ItemRepository {
  def create(item: ItemRecord): Task[ItemRecord]
  def all: Task[Seq[ItemRecord]]
  def findById(id: Int): Task[ItemRecord]
}

object ItemRepository {

  def create(item: ItemRecord): RIO[Has[ItemRepository], ItemRecord] = ZIO.serviceWith[ItemRepository](_.create(item))
  def all: RIO[Has[ItemRepository], Seq[ItemRecord]]                 = ZIO.serviceWith[ItemRepository](_.all)
  def findById(id: Int): RIO[Has[ItemRepository], ItemRecord]        = ZIO.serviceWith[ItemRepository](_.findById(id))

  val zioConn: TaskLayer[QDataSource] =
    ZEnv.live >>> QDataSource.fromPrefix("zioQuillExample")

  // live layer

}
