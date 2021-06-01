package dev.xymox.zio.playground.quill

import dev.xymox.zio.playground.quill.repository.{ItemRecord, ItemRepository}
import zio.{Has, RIO, RLayer, Task, ZIO}
import zio.console.Console

import java.time.Instant
import scala.language.implicitConversions

case class CreateItemRequest(name: String, description: String, price: Double)
case class Item(id: Long, name: String, description: String, price: Double, createdAt: Instant)

object Item {
  implicit def fromItemRecord(record: ItemRecord): Item               = Item(record.id, record.name, record.description, record.unitPrice, record.createdAt)
  implicit def fromSeqItemRecord(records: Seq[ItemRecord]): Seq[Item] = records.map(fromItemRecord)
}

trait ItemService {
  def create(request: CreateItemRequest): Task[Item]
  def all: Task[Seq[Item]]
  def get(id: Long): Task[Item]
}

object ItemService {
  def create(request: CreateItemRequest): RIO[Has[ItemService], Item] = ZIO.serviceWith[ItemService](_.create(request))
  def all: RIO[Has[ItemService], Seq[Item]]                           = ZIO.serviceWith[ItemService](_.all)
  def get(id: Int): RIO[Has[ItemService], Item]                       = ZIO.serviceWith[ItemService](_.get(id))

  val layer: RLayer[Has[ItemRepository] with Console, Has[ItemService]] = (ItemServiceLive(_, _)).toLayer
}

case class ItemServiceLive(repository: ItemRepository, console: Console.Service) extends ItemService {

  override def create(request: CreateItemRequest): Task[Item] = {
    repository.create(ItemRecord(name = request.name, description = request.description, unitPrice = request.price)).map(Item.fromItemRecord)
  }

  override def all: Task[Seq[Item]] = for {
    items <- repository.all
    _     <- console.putStrLn(s"Items: ${items.map(_.name).mkString(",")}")
  } yield items

  override def get(id: Long): Task[Item] = repository.findById(id).map(Item.fromItemRecord)
}
