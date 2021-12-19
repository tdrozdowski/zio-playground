package dev.xymox.zio.playground.quill.repository

import zio._
import zio.macros.accessible

import javax.sql.DataSource

@accessible
trait ItemRepository {
  def create(item: ItemRecord): Task[ItemRecord]
  def all: Task[Seq[ItemRecord]]
  def findById(id: Long): Task[ItemRecord]
}

object ItemRepository {
  val layer: URLayer[Has[DataSource], Has[ItemRepository]] = (ItemRepositoryLive(_)).toLayer
}
