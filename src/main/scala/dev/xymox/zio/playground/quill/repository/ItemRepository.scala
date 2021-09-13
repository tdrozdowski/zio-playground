package dev.xymox.zio.playground.quill.repository

import dev.xymox.zio.playground.quill.ZioQuillContext.QDataSource
import io.getquill.context.ZioJdbc.QDataSource
import zio._
import zio.macros.accessible

@accessible
trait ItemRepository {
  def create(item: ItemRecord): Task[ItemRecord]
  def all: Task[Seq[ItemRecord]]
  def findById(id: Long): Task[ItemRecord]
}

object ItemRepository {
  val layer: URLayer[QDataSource, Has[ItemRepository]] = (ItemRepositoryLive(_)).toLayer
}
