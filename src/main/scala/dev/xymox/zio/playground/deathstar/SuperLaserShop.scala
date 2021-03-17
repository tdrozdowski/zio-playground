package dev.xymox.zio.playground.deathstar

import dev.xymox.zio.playground.deathstar.DeathStarPlans.{DStream, SuperLaser, UnfinishedSpaceStation}
import zio._
import zio.clock.Clock
import zio.duration._
import zio.random.Random
import zio.stream._

class SuperLaserShop {
  val theGuySchedule = Schedule.fromDurations(5.seconds, 15.seconds)

  val lasers: ZStream[Random with Clock, Nothing, SuperLaser] =
    ZStream.repeatEffectWith(UIO(SuperLaser()), theGuySchedule)

  def installTheLaser(stations: DStream): ZStream[Random with Clock, Throwable, UnfinishedSpaceStation] =
    stations.zip(lasers).map { case (station, laser) => station.installLaser(laser) }
}
