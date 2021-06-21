package dev.xymox.zio.playground.prelude

import zio._
import zio.console._
import zio.prelude._

object ForEachExamples extends App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.provideLayer(ZEnv.live).exitCode

  case class Person(name: String, age: Int)

  val flipMe: Chunk[Either[String, Person]]    = Chunk(Right(Person("Jon", 32)), Right(Person("Clarice", 24))) //, Left("Some Error"), Left("Another Error"))
  val meFlipped: Either[String, Chunk[Person]] = flipMe.flip

  val program = putStrLn(s"Flip Me: $flipMe") *> putStrLn(s"Me flipped: $meFlipped")

}
