package dev.xymox.zio.playground

import zio._
import zio.console._
import zio.json._

import java.io.IOException
import java.time.Instant

object MoreJson extends App {

  case class Message(value: String, createAt: Instant)

  object Message {
    implicit val codec: JsonCodec[Message] = DeriveJsonCodec.gen[Message]
  }

  val program: ZIO[Console, IOException, Unit] =
    for {
      message <- ZIO.succeed(Message(value = "Hello World!", createAt = Instant.now))
      _       <- putStrLn(s"Message as JSON: ${message.toJsonPretty}")
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode
}
