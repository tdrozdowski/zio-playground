package dev.xymox.zio.playground.prelude

import zio.prelude.{Associative, AssociativeOps}
import zio._
import zio.console._

import java.time.Instant

object AssociativeExample extends App {

  implicit object InvoiceAssociative extends Associative[Invoice] {

    override def combine(l: => Invoice, r: => Invoice): Invoice = {
      val combinedItems: Seq[InvoiceItem] = l.items ++ r.items
      val newTotal: Double                = combinedItems.map(_.total).sum
      val newDate                         = if (l.date.toEpochMilli > r.date.toEpochMilli) l.date else r.date
      Invoice(date = newDate, items = combinedItems, total = newTotal)
    }
  }

  val program: ZIO[Console, Throwable, Unit] = {
    val items: Seq[InvoiceItem] = Seq(InvoiceItem(name = "Boomstick", description = "This...is my Boomstick!", unitPrice = 255.50, quantity = 1))
    for {
      invoice1   <- Task(Invoice(Instant.now, items.map(_.total).sum, items))
      invoice2   <- Task(Invoice(Instant.now, items.map(_.total).sum, items))
      newInvoice <- Task(invoice1 <> invoice2)
      _          <- putStrLn(s"Combined Invoice: $newInvoice")
    } yield ()
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode

}
