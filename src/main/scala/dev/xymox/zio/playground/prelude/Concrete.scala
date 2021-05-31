package dev.xymox.zio.playground.prelude

import dev.xymox.zio.playground.prelude.Concrete.Event.{Parallel, Sequential}

object Concrete {

  sealed trait Event {
    def ++(that: Event): Event = Sequential(this, that)
    def <>(that: Event): Event = Parallel(this, that)
  }

  object Event {
    case object DoNothing                                    extends Event
    final case class Single(description: String)             extends Event
    final case class Parallel(left: Event, right: Event)     extends Event
    final case class Sequential(first: Event, second: Event) extends Event
  }

  case class Order(id: Int, quantity: Int)

  case class OrderOperation(f: Order => Order) { self =>
    def <>(that: OrderOperation): OrderOperation = OrderOperation(self.f.andThen(that.f))
  }

  object OrderOperation {
    val noop: OrderOperation                  = OrderOperation(identity)
    val increaseQuantity: OrderOperation      = OrderOperation(order => order.copy(quantity = order.quantity + 1))
    val increaseQuantityByTwo: OrderOperation = increaseQuantity <> increaseQuantity <> increaseQuantity

  }

  // Associativity
  // (1 + 3) + 5 = 1 + (3 + 5)
  case class Average(currentSum: Double, currentCount: Long) {
    def <>(that: Average): Average = Average(currentSum + that.currentSum, currentCount + that.currentCount)
    def average: Double            = currentSum / currentCount
  }
}
