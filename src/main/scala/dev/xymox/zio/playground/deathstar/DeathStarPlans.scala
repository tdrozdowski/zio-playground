package dev.xymox.zio.playground.deathstar

import zio.clock.Clock
import zio.stream.ZStream

import java.util.UUID

object DeathStarPlans {

  type DStream = ZStream[Clock, Throwable, UnfinishedSpaceStation]

  case class SerialNumber(value: UUID = UUID.randomUUID())
  sealed trait PropulsionDrive
  case class HyperDriveEngine()           extends PropulsionDrive
  case class InfiniteImprobabilityDrive() extends PropulsionDrive
  case class TieFighter()
  case class ForceShield()
  case class SuperLaser(serialNumber: SerialNumber = SerialNumber())

  case class UnfinishedSpaceStation(
    engine: Option[PropulsionDrive] = None,
    shield: Option[ForceShield] = None,
    fleet: Seq[TieFighter] = Seq.empty,
    laser: Option[SuperLaser] = None
  ) {

    def installEngine(engine: PropulsionDrive): UnfinishedSpaceStation =
      copy(engine = Some(engine))

    def installShield(shield: ForceShield): UnfinishedSpaceStation =
      copy(shield = Some(shield))

    def deployFleet(fleet: Seq[TieFighter]): UnfinishedSpaceStation =
      copy(fleet = fleet)

    def installLaser(superLaser: SuperLaser): UnfinishedSpaceStation =
      copy(laser = Some(superLaser))
  }

  case class DeathStar(
    engine: PropulsionDrive,
    shield: ForceShield,
    fleet: Seq[TieFighter],
    laser: SuperLaser
  )
}
