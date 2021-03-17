package dev.xymox.zio.playground.deathstar

import dev.xymox.zio.playground.deathstar.DeathStarPlans.{DStream, TieFighter}
import zio.stream._

class TieFighterShop(groupSize: Int) {
  val tieFighters: Stream[Nothing, TieFighter] = Stream(TieFighter()).forever

  def deployFleet(stations: DStream): DStream =
    stations.zip(tieFighters.grouped(groupSize)).map { case (station, tiefighers) => station.deployFleet(tiefighers) }
}
