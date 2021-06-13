package dev.xymox.zio.playground.schema

import zio._
import zio.console._
import zio.schema._
import zio.schema.codec.{JsonCodec, ProtobufCodec}

object SchemaExamples extends App {

  final case class Person(name: String, age: Int)

  val aPerson: Person = Person("Jon", 32)

  val personSchema: Schema[Person] = DeriveSchema.gen[Person]

  val encodeDecodeProto: ZIO[Any, Throwable, (Chunk[Byte], Either[String, Person])] =
    for {
      encoder       <- ZIO.effect(ProtobufCodec.encode(personSchema))
      encodedPerson <- ZIO.effect(encoder(aPerson))
      decoder       <- ZIO.effect(ProtobufCodec.decode(personSchema))
      decodedPerson <- ZIO.effect(decoder(encodedPerson))
    } yield (encodedPerson, decodedPerson)

  val encodeDecodeJson: ZIO[Any, Throwable, (Chunk[Byte], String, Either[String, Person])] =
    for {
      encoder           <- ZIO.effect(JsonCodec.encode(personSchema))
      decoder           <- ZIO.effect(JsonCodec.decode(personSchema))
      encodedPerson     <- ZIO.effect(encoder(aPerson))
      encodedPersonJson <- ZIO.effect(encodedPerson.map(_.toChar).mkString)
      decodedPerson     <- ZIO.effect(decoder(encodedPerson))
    } yield (encodedPerson, encodedPersonJson, decodedPerson)

  def displayResults(r: (Chunk[Byte], Either[String, Person])): ZIO[Console, Serializable, Unit] =
    for {
      (encoded, maybeDecoded) <- ZIO.effectTotal(r)
      decoded                 <- ZIO.fromEither(maybeDecoded)
      _                       <- putStrLn("==============")
      _                       <- putStrLn(s"original: $aPerson\nEncoded: $encoded\nDecoded: $decoded")
    } yield ()

  def displayResultsJson =
    for {
      (encoded, encodedJson, maybeDecoded) <- encodeDecodeJson
      decoded                              <- ZIO.fromEither(maybeDecoded)
      _                                    <- putStrLn("==============")
      _                                    <- putStrLn(s"original: $aPerson\nEncoded: $encoded\nJSON: $encodedJson\nDecoded: $decoded")
    } yield ()

  sealed trait PaymentMethod

  object PaymentMethod {
    final case class CreditCard(number: String)                        extends PaymentMethod
    final case class ACH(accountNumber: String, routingNumber: String) extends PaymentMethod

    def fromString(name: String): PaymentMethod = name match {
      case "cc"  => CreditCard(number = "")
      case "ach" => ACH(accountNumber = "", routingNumber = "")
    }
  }

  val schemaPaymentMethod: Schema[PaymentMethod] = DeriveSchema.gen[PaymentMethod]

  val protoProgram: ZIO[Console, Serializable, Unit] = (encodeDecodeProto >>= displayResults) *> displayResultsJson

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = protoProgram.provideLayer(Console.live).exitCode
}
