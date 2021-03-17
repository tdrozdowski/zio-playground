package dev.xymox.zio.playground.deathstar

import dev.xymox.zio.playground.deathstar.DeathStarPlans.DeathStar
import zio.ZIO
import zio.clock.Clock
import zio.random.Random

class StarshipFactory(
  forceFieldShop: ForceFieldShop,
  propulsionEngineShop: PropulsionEngineShop,
  spaceStationBodyShop: SpaceStationBodyShop,
  superLaserShop: SuperLaserShop,
  tieFighterShop: TieFighterShop,
  auditor: EmpireAuditor
) {

  def orderDeathStar(quantity: Int): ZIO[Random with Clock, Throwable, Seq[DeathStar]] =
    spaceStationBodyShop.moonShapedSpaceStations
      .via(propulsionEngineShop.installEngine)
      .via(forceFieldShop.installShield)
      .via(tieFighterShop.deployFleet)
      .via(superLaserShop.installTheLaser)
      .via(auditor.inspect)
      .take(quantity)
      .runCollect

}
