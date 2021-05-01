package dev.xymox.zio.playground.prelude

import zio.prelude._
import zio._
import zio.console._

object StateExample extends App {

  case class GolfState(distance: Int)

  def swing(distance: Int): State[GolfState, Int] =
    State.modify { (s: GolfState) =>
      val newDistance = s.distance + distance
      (GolfState(newDistance), newDistance)
    }

  val stateWithDistance: State[GolfState, Int] = for {
    _             <- swing(10)
    _             <- swing(15).log("> 10 distance!")
    totalDistance <- swing(0)
  } yield totalDistance

  val program = for {
    results <- ZIO(stateWithDistance.run(GolfState(0)))
    _       <- putStrLn(s"Results: $results")
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode
}
