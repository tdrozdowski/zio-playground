package dev.xymox.zio.playground

import zio.json._

object Json extends App {
  case class Banana(curvature: Double)

  object Banana {
    //implicit val codec: JsonCodec[Banana] = DeriveJsonCodec.gen
    implicit val encoder: JsonEncoder[Banana] = DeriveJsonEncoder.gen
    implicit val decoder: JsonDecoder[Banana] = DeriveJsonDecoder.gen
  }

  val banana = """{ "curvature": 0.5}""".fromJson[Banana]
  println(s"a banana: $banana")

  val anotherBanana = Banana(0.7)
  println(s"a pretty banana ${banana.toJsonPretty}")
}
