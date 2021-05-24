package dev.xymox.zio.playground.quill.repository

import io.getquill.context.ZioJdbc.{QConnection, QDataSource}
import zio.{RIO, Task, ZLayer}
import zio.blocking.Blocking

trait ItemRepository {
  def create(item: ItemRecord): Task[ItemRecord]
  def all: Task[Seq[ItemRecord]]
  def findById(id: Int): Task[ItemRecord]
}

object ItemRepository {

  val zioConn: ZLayer[Blocking, Throwable, QConnection] =
    QDataSource.fromPrefix("zioQuillExample") >>> QDataSource.toConnection

}
