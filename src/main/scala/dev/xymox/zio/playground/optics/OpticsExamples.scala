package dev.xymox.zio.playground.optics

import zio._
import zio.console._
import zio.optics._

import java.io.IOException
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

  val items: Seq[Item] = Seq(
    Item(1, "Boomstick", "This..is my boomstick!", 255.50, Instant.now),
    Item(2, "Helm", "A rather cheap looking helm", 19.99, Instant.now)
  )

  // calcualte a 10% discount for each item and update it
  val tenPercentDiscount: Seq[Item] =
    items.map(item => Item.price.update(item)(price => Item.calcDiscount(price))).map(_.getOrElse(throw new Exception("Shouldn't happen")))

  def printItems(items: Seq[Item]) = items.map(item => (Item.name.get(item), Item.price.get(item)))

  val program: ZIO[Console, Throwable, Unit] =
    for {
      _       <- putStrLn(s"All items: \n\t ${items.map(i => (i.name, i.price)).mkString(", \n\t ")}")
      _       <- putStrLn("Applying 10% discount to all items!")
      updated <- ZIO.effect(tenPercentDiscount)
      _       <- putStrLn(s"All items discounted: \n\t ${updated.map(i => (i.name, i.price)) mkString (", \n\t ")}")
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.provideLayer(Console.live).exitCode
}

object OpticsWithRefExample extends App {

  val items: Chunk[Item] = Chunk(
    Item(1, "Boomstick", "This..is my boomstick!", 255.50, Instant.now),
    Item(2, "Helm", "A rather cheap looking helm", 19.99, Instant.now)
  )

  def discountItems(items: Chunk[Item]): Chunk[Item] = items.map(item => Item.price.update(item)(price => Item.calcDiscount(price))).map(_.toOption.get)

  val program: ZIO[Console, IOException, Unit] =
    for {
      ref <- Ref.make(items)
      _   <- putStrLn(s"Wrapped items in ref: $ref")
      _   <- ref.update(discountItems)
      _   <- putStrLn(s"After update: $ref")
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
