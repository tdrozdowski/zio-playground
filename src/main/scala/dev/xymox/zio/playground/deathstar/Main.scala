package dev.xymox.zio.playground.deathstar

import zio._
import zio.console._
import zio.duration._

object Main extends App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val shop    = new SpaceStationBodyShop(1.second)
    val program = shop.moonShapedSpaceStations.take(1).runCollect

    program.foldM(
      e => putStrLn(s"Execution failed with: ${e.printStackTrace()}").as(ExitCode.failure),
      _ => putStrLn(s"Death Star is fully operational!").as(ExitCode.success)
    )
  }
}
