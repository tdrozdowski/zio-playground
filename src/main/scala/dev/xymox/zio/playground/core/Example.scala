package dev.xymox.zio.playground.core

import zio.UIO
import zio.stream.{ZSink, ZStream}

object Example {

  trait Service {
    val static: UIO[String]
    def zeroArgs: UIO[Int]
    def zeroArgsWithParens(): UIO[Long]
    def singleArg(arg1: Int): UIO[String]
    def multiArgs(arg1: Int, arg2: Long): UIO[String]
    def multiParamLists(arg1: Int)(arg2: Long): UIO[String]
    def command(arg1: Int): UIO[Unit]
    def overloaded(arg1: Int): UIO[String]
    def overloaded(arg1: Long): UIO[String]
    def function(arg1: Int): String
    def sink(a: Int): ZSink[Any, String, Int, Int, List[Int]]
    def stream(a: Int): ZStream[Any, String, Int]

  }
}
