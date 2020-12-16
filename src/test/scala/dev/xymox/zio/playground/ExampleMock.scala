package dev.xymox.zio.playground

import zio.stream.{ZSink, ZStream}
import zio.test.mock
import zio.test.mock.Mock
import zio.{Has, UIO, URLayer, ZLayer}

object ExampleMock extends Mock[Example] {
  object Static             extends Effect[Unit, Nothing, String]
  object ZeroArgs           extends Effect[Unit, Nothing, Int]
  object ZeroArgsWithParens extends Effect[Unit, Nothing, Long]
  object SingleArg          extends Effect[Int, Nothing, String]
  object MultiArgs          extends Effect[(Int, Long), Nothing, String]
  object MultiParamLists    extends Effect[(Int, Long), Nothing, String]
  object Command            extends Effect[Int, Nothing, Unit]

  object Overloaded {
    object _0 extends Effect[Int, Nothing, String]
    object _1 extends Effect[Long, Nothing, String]
  }
  object Function extends Method[Int, Throwable, String]
  object Sink   extends Sink[Any, String, Int, Int, List[Int]]
  object Stream extends Stream[Any, String, Int]

  override val compose: URLayer[Has[mock.Proxy], Example] = ZLayer.fromServiceM { proxy =>
    withRuntime.map { rts =>
      new Example.Service {
        override val static: UIO[String] = proxy(Static)

        override def zeroArgs: UIO[Int] = proxy(ZeroArgs)

        override def zeroArgsWithParens(): UIO[Long] = proxy(ZeroArgsWithParens)

        override def singleArg(arg1: Int): UIO[String] = proxy(SingleArg, arg1)

        override def multiArgs(arg1: Int, arg2: Long): UIO[String] = proxy(MultiArgs, arg1, arg2)

        override def multiParamLists(arg1: Int)(arg2: Long): UIO[String] = proxy(MultiParamLists, arg1, arg2)

        override def command(arg1: Int): UIO[Unit] = proxy(Command, arg1)

        override def overloaded(arg1: Int): UIO[String] = proxy(Overloaded._0, arg1)

        override def overloaded(arg1: Long): UIO[String] = proxy(Overloaded._1, arg1)

        override def function(arg1: Int): String = rts.unsafeRunTask(proxy(Function, arg1))

        override def sink(a: Int): ZSink[Any, String, Int, Int, List[Int]] =
          rts.unsafeRun(proxy(Sink, a).catchAll(error => UIO(ZSink.fail[String, Int](error))))

        override def stream(a: Int): ZStream[Any, String, Int] = rts.unsafeRun(proxy(Stream, a))
      }
    }
  }
}
