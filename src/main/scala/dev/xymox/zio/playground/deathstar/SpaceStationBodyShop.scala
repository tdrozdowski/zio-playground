package dev.xymox.zio.playground.deathstar

import dev.xymox.zio.playground.deathstar.DeathStarPlans.{DStream, UnfinishedSpaceStation}
import zio.{Schedule, ZIO}
import zio.duration.Duration
import zio.stream.ZStream

class SpaceStationBodyShop(buildTime: Duration) {

  val moonShapedSpaceStations: DStream =
    ZStream.repeatEffectWith(ZIO(UnfinishedSpaceStation()), Schedule.spaced(buildTime))
}
