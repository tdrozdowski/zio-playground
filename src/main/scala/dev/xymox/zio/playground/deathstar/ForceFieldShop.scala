package dev.xymox.zio.playground.deathstar

import dev.xymox.zio.playground.deathstar.DeathStarPlans.{DStream, ForceShield}
import zio.UIO
import zio.stream._

class ForceFieldShop(shipmentSize: Int) {
  case class ShieldShipment(shields: Seq[ForceShield])

  val shipments: Stream[Nothing, ShieldShipment] = Stream.repeatEffect(UIO(ShieldShipment(Seq.fill(shipmentSize)(ForceShield()))))

  val shields: Stream[Nothing, ForceShield] = shipments.mapConcat(_.shields)

  def installShield(stations: DStream): DStream =
    stations.zip(shields).map { case (station, shields) => station.installShield(shields) }
}
