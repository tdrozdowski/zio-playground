package dev.xymox.zio.playground.quill

import dev.xymox.zio.playground.quill.repository.{ItemRecord, ItemRepository}
import io.scalaland.chimney.dsl.TransformerOps
import zio.{Has, RIO, RLayer, Task, ZIO}
import zio.console.Console
import zio.json.{DeriveJsonCodec, JsonCodec}
import zio.macros.accessible

import java.time.Instant
import scala.language.implicitConversions

case class CreateItemRequest(name: String, description: String, price: Double)

object CreateItemRequest {
  implicit val codec: JsonCodec[CreateItemRequest] = DeriveJsonCodec.gen[CreateItemRequest]
}
case class Item(id: Long, name: String, description: String, price: Double, createdAt: Instant)

object Item {
  implicit def fromItemRecord(record: ItemRecord): Item               = record.into[Item].withFieldRenamed(_.unitPrice, _.price).transform
  implicit def fromSeqItemRecord(records: Seq[ItemRecord]): Seq[Item] = records.map(fromItemRecord)
  implicit val codec: JsonCodec[Item]                                 = DeriveJsonCodec.gen[Item]
}

@accessible
trait ItemService {
  def create(request: CreateItemRequest): Task[Item]
  def all: Task[Seq[Item]]
  def get(id: Long): Task[Item]
}

object ItemService {
  val layer: RLayer[Has[ItemRepository] with Console, Has[ItemService]] = (ItemServiceLive(_, _)).toLayer
}

case class ItemServiceLive(repository: ItemRepository, console: Console.Service) extends ItemService {

  override def create(request: CreateItemRequest): Task[Item] = {
    repository.create(ItemRecord(name = request.name, description = request.description, unitPrice = request.price)).map(Item.fromItemRecord)
  }

  override def all: Task[Seq[Item]] = for {
    items <- repository.all
    _     <- console.putStrLn(s"Items: ${items.map(_.name).mkString(",")}")
  } yield items.sortBy(_.id)

  override def get(id: Long): Task[Item] = repository.findById(id).map(Item.fromItemRecord)
}
