package dev.xymox.zio.playground.prelude

class ParameterizedExamples {

//  // containers or producers of A values
//  trait Covariant[+A]
//  // containers
//  // Option
//  // Either
//  // List
//
//  // producers
//  // ZIO
//  // ZManaged
//  // ZStream
//  // Future
//  // Iterator
//
//  // Stream[+A] | Stream[String]
//  // Transducer[-A, +B] | Transducer[String, Double] ==> take the length of each string
//  // Sink[-A] | Sink[Double]
//
//  // takes in type A and produces type A
//  trait Invariant[A]
//  // Codec[A]
//
//  // consumes a value and does something with it
//  trait Contravariant[-A]
//  // Functions with their input type A => Boolean
//  // Ordering (A, A) => OrderingResult
//  // ZSink => Chunk[A] => (Chunk[L], Result)
//  // ZIO Environment
//  // Encoder
//  // Encoder (A => Bytes)
//
//  // Covariant
//  case class Box[+A](value: A) {
//    def map[B](f: A => B): Box[B]                  = Box(f(value))
//    // zip
//    def both[B](that: Box[B]): Box[(A, B)]         = Box(value -> that.value)
//    // failure / racing
//    def either[B](that: Box[B]): Box[Either[A, B]] = ???
//  }
//
//  // 1. Can I combine?
//  // 2. Zip -> Product -> Tuple
//  // 3. OrElse -> Sum -> Either
//  case class FailBox[+A](value: Either[Throwable, A]) { self =>
//    def map[B](f: A => B): FailBox[B] = FailBox(value.map(f))
//
//    def both[B](that: FailBox[B]): FailBox[(A, B)] =
//      for {
//        a <- self
//        b <- that
//      } yield (a, b)
//
//    def either[B](that: FailBox[B]): FailBox[Either[A, B]] = FailBox(self match {
//      case Left(_)  => that.value.map(Right(_))
//      case Right(a) => Left(Right(a))
//    })
//  }
//
//  trait Stream[+A] { self =>
//    // cartesian product
//    def cross[B](that: Stream[B]): Stream[(A, B)] = ???
//    // parwise zipping
//    def zip[B](that: Stream[B]): Stream[(A, B)]   = ???
//
//    // run the first stream and if it fails run the second
//    def orElseEither[B](that: Stream[B]): Stream[Either[A, B]] = ???
//
//    def merge[B](that: Stream[B]): Stream[Either[A, B]] = ???
//  }
//
//  // Contravariant
//  trait Sink[-A] { self =>
//    def process(a: A): Unit
//    def contramap[B](f: B => A): Sink[B]
//    def both[B](that: Sink[B]): Sink[(A, B)]
//    def bothWith[B, C](that: Sink[B])(f: C => (A, B)): Sink[C] = both(that).contramap(f)
//    def either[B](that: Sink[B]): Sink[Either[A, B]]
//
//  }

}
