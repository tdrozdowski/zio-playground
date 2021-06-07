package dev.xymox.zio.playground.optics

import zio._
import zio.console._
import zio.optics._

import java.time.Instant

case class Item(id: Long, name: String, description: String, price: Double, createdAt: Instant)

object Item {

  def name: Lens[Item, String] = Lens(
    item => Right(item.name),
    name => item => Right(item.copy(name = name))
  )

  def price: Lens[Item, Double] = Lens(
    item => Right(item.price),
    price => item => Right(item.copy(price = price))
  )

  def calcDiscount(price: Double): Double = price - (price * .1)
}

object OpticsExamples extends App {

  val items: Chunk[Item] = Chunk(
    Item(1, "Boomstick", "This..is my boomstick!", 255.50, Instant.now),
    Item(2, "Helm", "A rather cheap looking helm", 19.99, Instant.now)
  )

  def hasPrice(item: Item) = item.price > 0.00

  def discountItems: Traversal[Chunk[Item], Double] =
    Optic.filter(hasPrice).foreach(Item.price)

  val program: ZIO[Console, Throwable, Unit] =
    for {
      _       <- putStrLn(s"All items: \n\t ${items.map(i => (i.name, i.price)).mkString(", \n\t ")}")
      _       <- putStrLn("Applying 10% discount to all items!")
      updated <- ZIO.fromEither(discountItems.update(items)(_.map(Item.calcDiscount)))
      _       <- putStrLn(s"All items discounted: \n\t ${updated.map(i => (i.name, i.price)) mkString (", \n\t ")}")
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.provideLayer(Console.live).exitCode
}

object OpticsWithRefExample extends App {

  val items: Chunk[Item] = Chunk(
    Item(1, "Boomstick", "This..is my boomstick!", 255.50, Instant.now),
    Item(2, "Helm", "A rather cheap looking helm", 19.99, Instant.now)
  )

  def hasPrice(item: Item) = item.price > 0.00

  def discountItems: Traversal[Chunk[Item], Double] =
    Optic.filter(hasPrice).foreach(Item.price)

  val program: ZIO[Console, Exception, Unit] =
    for {
      ref     <- Ref.make(items)
      _       <- putStrLn(s"Wrapped items in ref: $ref")
      updated <- ZIO.fromEither(discountItems.update(items)(_.map(Item.calcDiscount)))
      _       <- ref.set(updated)
      _       <- putStrLn(s"After update: $ref")
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode
}

object PersonExample extends App {
  case class Person(name: String, age: Int, birthMonth: Int)

  object Person {

    val age: Lens[Person, Int] =
      Lens(
        person => Right(person.age),
        age => person => Right(person.copy(age = age))
      )
  }

  val people: Chunk[Person] = Chunk(
    Person("Ash", 42, 1),
    Person("Lee", 23, 5),
    Person("Sam", 31, 1)
  )

  val optic: Optional[Either[String, Person], Int] =
    Optic.right >>> Person.age

  def hasJanuaryBirthday(person: Person): Boolean = person.birthMonth == 1

  val janAges: Traversal[Chunk[Person], Int] =
    Optic.filter(hasJanuaryBirthday).foreach(Person.age)

  val program: ZIO[Console, Throwable, Unit] =
    for {
      _              <- putStrLn(s"People before: $people")
      updatedWithAge <- ZIO.fromEither(janAges.update(people)(_.map(_ + 1)))
      _              <- putStrLn(s"Results: $updatedWithAge")
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.provideLayer(Console.live).exitCode
}
