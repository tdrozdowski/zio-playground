package dev.xymox.zio.playground.quill

import dev.xymox.zio.playground.quill.repository.ItemRepository
import zio._
import zio.blocking.Blocking
import zio.console._
import zio.magic._

object ZioQuillRepositoryExample extends App {

  val items = Seq(
    CreateItemRequest("Boomstick", "This is my...boomstick!", 255.00),
    CreateItemRequest("Armor", "Chainmail body armor", 500.00),
    CreateItemRequest("Helm", "A cheap looking helm", 19.99)
  )

  val startup: ZIO[Has[ItemService], Throwable, Seq[Item]] = ZIO.foreachPar(items)(ItemService.create)

  val program: ZIO[Console with Has[ItemService], Throwable, Seq[Item]] =
    (startup *> ItemService.all.tap(a => putStrLn(s"Found: \n\t${a.mkString("\n\t")}")))

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    program.injectSome(Console.live, ZioQuillContext.dataSourceLayer, ItemService.layer, ItemRepository.layer).exitCode
}
