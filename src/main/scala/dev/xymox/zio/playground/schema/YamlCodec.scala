package dev.xymox.zio.playground.schema

import zio.Chunk
import zio.schema.Schema
import zio.schema.codec.Codec
import zio.stream.ZTransducer

object YamlCodec extends Codec {
  override def encoder[A](schema: Schema[A]): ZTransducer[Any, Nothing, A, Byte] = ???

  override def decoder[A](schema: Schema[A]): ZTransducer[Any, String, Byte, A] = ???

  override def encode[A](schema: Schema[A]): A => Chunk[Byte] = ???

  override def decode[A](schema: Schema[A]): Chunk[Byte] => Either[String, A] = ???
}
