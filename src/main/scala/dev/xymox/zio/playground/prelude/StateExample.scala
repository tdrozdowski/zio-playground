package dev.xymox.zio.playground.prelude

import zio.prelude._
import zio._
import zio.console._

object StateExample extends App {

  case class GolfState(distance: Int, swings: Int)

  def swing(distance: Int): State[GolfState, Int] =
    State.modify { (s: GolfState) =>
      val newDistance = s.distance + distance
      (GolfState(newDistance, s.swings + 1), newDistance)
    }

  def distanceFromHole(teeDistance: Int): State[GolfState, Int] =
    State.modify { (s: GolfState) => (s, teeDistance - s.distance) }

  val stateWithDistance: State[GolfState, Int] = for {
    _             <- swing(10)
    _             <- swing(15)
    totalDistance <- swing(0)
  } yield totalDistance

  val remainingDistance: State[GolfState, Int] = for {
    remaining <- distanceFromHole(300)
  } yield remaining

  def holeScore(par: Int): State[GolfState, String] =
    State.modify { (s: GolfState) =>
      val score      = s.swings - par
      val scoreLabel = score match {
        case 0                  => "Even Par"
        case 1                  => "Bogie"
        case 2                  => "Double Bogie"
        case 3                  => "Triple Bogie"
        case -1                 => "Birdie"
        case -2                 => "Eagle"
        case -3                 => "Double Eagle"
        case -4                 => "Condor"
        case -5                 => "Ace"
        case _ if (score > par) => "Over Par"
      }
      (s, scoreLabel)
    }

  val score: State[GolfState, String] = for {
    score <- holeScore(4)
  } yield score

  val program = for {
    distance  <- ZIO(stateWithDistance.run(GolfState(0, 0)))
    remaining <- ZIO(remainingDistance.run(distance._1))
    score     <- ZIO(score.run(remaining._1))
    _         <- putStrLn(s"Results: $distance; remaining: $remaining.\nYour score for this hole was: $score")
  } yield ()

  val program2 = for {
    // Compose stateWithDistance with remainingDistance and then calculate hole score
    something <- ZIO((stateWithDistance <<< remainingDistance >>> score).run(GolfState(0, 0)))
    _         <- putStrLn(s"Results: $something")
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode *> program2.exitCode
}
