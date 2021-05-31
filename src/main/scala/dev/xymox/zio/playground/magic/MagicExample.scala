package dev.xymox.zio.playground.magic

import zio._
import zio.console._
import zio.magic._

import java.util.UUID

object MagicExample extends App {

  val items = Seq(
    CreateItemRequest("Boomstick", "This is my...boomstick!", 255.00),
    CreateItemRequest("Armor", "Chainmail body armor", 500.00),
    CreateItemRequest("Helm", "A cheap looking helm", 19.99)
  )

  val startup: ZIO[Has[ItemService], Throwable, Seq[UUID]] = ZIO.foreachPar(items)(ItemService.create)
  val program: Task[Seq[Item]]                             = (startup *> ItemService.all).inject(Console.live, ItemService.layer, ItemRepository.layer)

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode
}

case class CreateItemRequest(name: String, description: String, price: Double)
case class Item(id: UUID, name: String, description: String, price: Double)

trait ItemRepository {
  def allItems: Task[Seq[Item]]
  def getItem(id: UUID): Task[Option[Item]]
  def putItem(item: Item): Task[UUID]
}

object ItemRepository {

  val layer: ULayer[Has[ItemRepository]] = {
    for {
      ref <- Ref.make(Map.empty[UUID, Item])
    } yield ItemRepositoryLive(ref)
  }.toLayer
}

case class ItemRepositoryLive(ref: Ref[Map[UUID, Item]]) extends ItemRepository {
  override def getItem(id: UUID): Task[Option[Item]] = ref.get.map(_.get(id))

  override def putItem(item: Item): Task[UUID] = {
    for {
      _ <- ref.update(_.updated(item.id, item))
    } yield item.id
  }

  override def allItems: Task[Seq[Item]] = ref.get.map(_.values.toList)
}

trait ItemService {
  def create(request: CreateItemRequest): Task[UUID]
  def all: Task[Seq[Item]]
  def get(id: UUID): Task[Option[Item]]
}

object ItemService {
  def create(request: CreateItemRequest): RIO[Has[ItemService], UUID] = ZIO.serviceWith[ItemService](_.create(request))
  def all: RIO[Has[ItemService], Seq[Item]]                           = ZIO.serviceWith[ItemService](_.all)
  def get(id: UUID): RIO[Has[ItemService], Option[Item]]              = ZIO.serviceWith[ItemService](_.get(id))

  val layer: RLayer[Has[ItemRepository] with Console, Has[ItemService]] = (ItemServiceLive(_, _)).toLayer
}

case class ItemServiceLive(repository: ItemRepository, console: Console.Service) extends ItemService {

  override def create(request: CreateItemRequest): Task[UUID] = {
    repository.putItem(Item(UUID.randomUUID(), request.name, request.description, request.price))
  }

  override def all: Task[Seq[Item]] = for {
    items <- repository.allItems
    _     <- console.putStrLn(s"Items: ${items.map(_.name).mkString(",")}")
  } yield items

  override def get(id: UUID): Task[Option[Item]] = repository.getItem(id)
}
