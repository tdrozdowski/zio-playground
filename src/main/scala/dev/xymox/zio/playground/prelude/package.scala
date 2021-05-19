package dev.xymox.zio.playground

import java.time.Instant

package object prelude {

  case class InvoiceItem(name: String, description: String, unitPrice: Double, quantity: Int) {
    def total: Double = unitPrice * quantity
  }
  case class Invoice(date: Instant, total: Double, items: Seq[InvoiceItem])
}
