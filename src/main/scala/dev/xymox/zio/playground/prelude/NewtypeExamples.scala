package dev.xymox.zio.playground.prelude

import java.util.UUID
import scala.util.matching.Regex
import zio.prelude._
import zio.test.Assertion
import zio.test.Assertion._

object NewtypeExamples extends App {

  val VIN_REGEX: Regex = "[A-HJ-NPR-Z0-9]{17}".r

  val UUID_MAX_LENGTH: Int = UUID.randomUUID().toString.length

  object Id extends SubtypeSmart[String](hasSizeString(Assertion.equalTo(36))) {
    def create: Id                   = Id(UUID.randomUUID().toString)
    def fromString(uuid: String): Id = Id(uuid)
  }
  type Id = Id.Type

  val testId: Id    = Id.fromString("asdf")
  val created       = Id.create
  val customFail    = Id.make("failed")
  val customSucceed = Id.make(UUID.randomUUID().toString).toOption.getOrElse(throw new Exception("Won't happen"))

  println(s"TestId: $testId")
  println(s"created: $created")
  println(s"customFail: $customFail")
  println(s"customSucceed: $customSucceed")

  object Meter extends Newtype[Double]
  type Meter = Meter.Type

  implicit class MeterSyntax(private val self: Meter) extends AnyVal {

    def +(that: Meter): Meter =
      Meter.wrap(Meter.unwrap(self) + Meter.unwrap(that))
  }

  val x = Meter(3.4)
  val y = Meter(100.34)
  val z = x + y

  println(s"total: $z")
}
