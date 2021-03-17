package dev.xymox.zio.playground.deathstar

import dev.xymox.zio.playground.deathstar.DeathStarPlans.{DStream, PropulsionDrive}
import zio.stream._

class PropulsionEngineShop(engine: PropulsionDrive) {
  val engines: Stream[Nothing, PropulsionDrive] = Stream(engine).forever

  def installEngine(stations: DStream): DStream =
    stations.zip(engines).map { case (station, engine) => station.installEngine(engine) }
}
