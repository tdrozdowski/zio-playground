package dev.xymox.zio.playground.quill

import io.getquill.context.ZioJdbc.{QConnection, QDataSource}
import zio.RLayer
import zio.blocking.Blocking

object ZioQuillContext {
  val dataSourceLayer: RLayer[Blocking, QDataSource] = QDataSource.fromPrefix("zioQuillExample")
  val connectionLayer: RLayer[Blocking, QConnection] = dataSourceLayer >>> QDataSource.toConnection
}
