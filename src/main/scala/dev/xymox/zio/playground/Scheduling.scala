package dev.xymox.zio.playground

import zio.Schedule.Decision
import zio._
import zio.console._
import zio.duration._

import scala.util.Random

object Scheduling extends App {

  def makeRequest: Task[String] = Task.effect {
    if (Random.nextInt(10) > 7) "some value" else throw new Exception("BIG BA-DA-BOOOOOOOM")
  }

  def schedule[A] = Schedule.spaced(1.second) && Schedule.recurs(4).onDecision {
    case Decision.Done(_)                 => putStrLn("done trying...").orDie
    case Decision.Continue(attempt, _, _) => putStrLn(s"attempt #$attempt").orDie
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    for {
      _ <- makeRequest
        .retry(schedule)
        .foldM(
          ex => putStrLn(s"Exception failed: ${ex.getMessage}").orDie,
          v => putStrLn(s"Succeeded with $v").orDie
        )
    } yield ExitCode.success
  }
}
