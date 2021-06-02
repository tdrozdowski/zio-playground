package dev.xymox.zio.playground.quill

import io.getquill.context.ZioJdbc.QDataSource
import zio.ZLayer
import zio.blocking.Blocking

object ZioQuillContext {
  val dataSourceLayer: ZLayer[Any, Nothing, QDataSource] = Blocking.live >>> QDataSource.fromPrefix("zioQuillExample").orDie
}
