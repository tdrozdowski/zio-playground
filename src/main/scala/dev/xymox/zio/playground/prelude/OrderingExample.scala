package dev.xymox.zio.playground.prelude

import dev.xymox.zio.playground.prelude.OrderingExample.Hand.Hand
import zio._
import zio.console._
import zio.prelude._

object OrderingExample extends App {

  object Hand extends Enumeration {
    type Hand = Value

    val Paper    = Value(1, "Paper")
    val Scissors = Value(2, "Scissors")
    val Rock     = Value(3, "Rock")

  }

  implicit val handOrd: Ord[Hand] = Ord[Int].contramap[Hand](_.id)

  def beats(a: Hand, b: Hand): Ordering = (a, b) match {
    case (Hand.Rock, Hand.Paper) => Ordering.LessThan
    case (Hand.Paper, Hand.Rock) => Ordering.GreaterThan
    case _                       => a =?= b
  }

  def compare(first: Int, second: Int) =
    for {
      firstHand  <- ZIO.effectTotal(Hand.values.toList(first))
      secondHand <- ZIO.effectTotal(Hand.values.toList(second))
      _          <- putStrLn(s"${firstHand} vs ${secondHand}: ${beats(firstHand, secondHand)}")
    } yield ()

  val program =
    for {
      _ <- ZIO.foreach_((0 until 3).toList) { first =>
        ZIO.foreach((0 until 3).toList) { second =>
          compare(first, second)
        }
      }
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode.provideLayer(Console.live)
}
