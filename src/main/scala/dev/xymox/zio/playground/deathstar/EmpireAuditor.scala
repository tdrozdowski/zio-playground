package dev.xymox.zio.playground.deathstar

import dev.xymox.zio.playground.deathstar.DeathStarPlans.{DeathStar, UnfinishedSpaceStation}
import dev.xymox.zio.playground.deathstar.EmpireAuditor.StationRejected
import zio.clock.Clock
import zio.random.Random
import zio.stream.ZStream

object EmpireAuditor {
  case class StationRejected(station: UnfinishedSpaceStation) extends IllegalStateException(s"$station does not meet the necessary criteria!")
}

class EmpireAuditor {

  def inspect(stations: ZStream[Random with Clock, Throwable, UnfinishedSpaceStation]): ZStream[Random with Clock, Throwable, DeathStar] =
    stations.collect {
      case UnfinishedSpaceStation(Some(engine), Some(shield), fleet, Some(laser)) if fleet.nonEmpty => DeathStar(engine, shield, fleet, laser)
      case unfinished                                                                               => throw StationRejected(unfinished)
    }
}
