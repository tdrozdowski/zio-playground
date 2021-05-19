package dev.xymox.zio.playground.prelude

import zio._
import zio.console.{putStrLn, Console}
import zio.prelude.newtypes.Max
import zio.prelude.{Associative, AssociativeBoth, AssociativeOps, Equal, Idempotent}

import java.time.Instant

object IdempotentExample extends App {

  implicit object InvoiceEqual extends Equal[Invoice] {

    override protected def checkEqual(l: Invoice, r: Invoice): Boolean =
      l.date == r.date && l.total == r.total && l.items == r.items
  }

  implicit object InvoiceIdempotent extends Idempotent[Invoice] with Associative[Invoice] {

    override def combine(l: => Invoice, r: => Invoice): Invoice = {
      val combinedItems: Seq[InvoiceItem] = l.items ++ r.items
      val newTotal: Double                = combinedItems.map(_.total).sum
      val newDate                         = if (l.date.toEpochMilli > r.date.toEpochMilli) l.date else r.date
      Invoice(date = newDate, items = combinedItems, total = newTotal)
    }
  }

  val program: ZIO[Console, Throwable, Unit] = {
    val items1: Seq[InvoiceItem] =
      Seq(InvoiceItem(name = "Boomstick", description = "This...is my Boomstick!", unitPrice = 255.50, quantity = 1))
    val items2: Seq[InvoiceItem] =
      Seq(InvoiceItem(name = "Zionomicon", description = "Neo-ancient tome of FP black magic.", unitPrice = 75.00, quantity = 1))
    for {
      invoice1   <- Task(Invoice(Instant.now, items1.map(_.total).sum, items1))
      invoice2   <- Task(Invoice(Instant.now, items2.map(_.total).sum, items2))
      newInvoice <- Task(invoice1 <> invoice2 <> invoice1)
      // what do we get?
      _          <- putStrLn(s"Combined Invoice: $newInvoice")
    } yield ()
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode
}
