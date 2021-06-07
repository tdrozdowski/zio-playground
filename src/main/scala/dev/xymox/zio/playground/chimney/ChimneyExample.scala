package dev.xymox.zio.playground.chimney

import dev.xymox.zio.playground.optics.Item
import dev.xymox.zio.playground.quill.repository.ItemRecord
import io.scalaland.chimney.dsl._
import zio._
import zio.console._

import java.time.Instant

object ChimneyExample extends App {

  val itemRecord: ItemRecord = ItemRecord(1, "Boomstick", "This is my...boomstick!", 255.00, Instant.now)

  val program: ZIO[Console, Throwable, Unit] =
    for {
      _    <- putStrLn(s"Item record: $itemRecord")
      item <- ZIO.effect(itemRecord.into[Item].withFieldRenamed(_.unitPrice, _.price).transform)
      _    <- putStrLn(s"..transformed into an item: $item")
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.provideLayer(Console.live).exitCode
}
