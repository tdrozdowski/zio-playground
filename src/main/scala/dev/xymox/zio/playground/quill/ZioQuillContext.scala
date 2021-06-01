package dev.xymox.zio.playground.quill

import io.getquill.context.ZioJdbc.{QConnection, QDataSource}
import zio.{Has, RLayer, ZLayer}
import zio.blocking.Blocking

import java.io.Closeable
import javax.sql.DataSource

object ZioQuillContext {
  val dataSourceLayer: ZLayer[Blocking, Nothing, QDataSource] = QDataSource.fromPrefix("zioQuillExample").orDie
  //val connectionLayer: RLayer[Blocking, QConnection]              = dataSourceLayer >>> QDataSource.toConnection
}
