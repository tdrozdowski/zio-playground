package dev.xymox.zio.playground.core

import zio.json._

object Json extends App {
  case class Banana(curvature: Double)

  object Banana {
    implicit val codec: JsonCodec[Banana] = DeriveJsonCodec.gen
  }

  val banana = """{ "curvature": 0.5}""".fromJson[Banana]
  println(s"a banana: $banana")

  val anotherBanana = Banana(0.7)
  println(s"a pretty banana\n${anotherBanana.toJsonPretty}")

  val badBanana = """{"curvature": "womp womp"}""".fromJson[Banana]
  println(s"a bad banana: $badBanana")
}

object AdvJson extends App {
  sealed trait Fruit
  case class Apple(poison: Boolean)    extends Fruit
  case class Banana(curvature: Double) extends Fruit

  object Fruit {
    implicit val encoder: JsonEncoder[Fruit] = DeriveJsonEncoder.gen
    implicit val decoder: JsonDecoder[Fruit] = DeriveJsonDecoder.gen
  }

  val banana = """{"Banana":{"curvature":0.5}}""".fromJson[Fruit]
  val apple  = """{"Apple":{"poison":false}}""".fromJson[Fruit]

  println(s"apple: $apple\nbanana: $banana")

  println(s"apple:\n${apple.toOption.get.toJsonPretty}")
  println(s"banana:\n${banana.toOption.get.toJson}")
}

object AdvJsonConfig extends App {
  sealed trait Fruit
  @jsonHint("omenan") case class Apple(@jsonField("bad") poison: Boolean)            extends Fruit
  @jsonHint("bananaai") case class Banana(@jsonField("bendiness") curvature: Double) extends Fruit

  object Fruit {
    implicit val encoder: JsonEncoder[Fruit] = DeriveJsonEncoder.gen
    implicit val decoder: JsonDecoder[Fruit] = DeriveJsonDecoder.gen
  }

  val apple: Fruit  = Apple(poison = true)
  val banana: Fruit = Banana(curvature = 0.132222)

  println(s"apple:\n${apple.toJsonPretty}")
  println(s"banana:\n${banana.toJson}")
}
