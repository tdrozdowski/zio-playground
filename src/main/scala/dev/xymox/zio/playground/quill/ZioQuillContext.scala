package dev.xymox.zio.playground.quill

import io.getquill.context.ZioJdbc.QDataSource
import zio.{Has, ZLayer}
import zio.blocking.Blocking

import java.io.Closeable
import javax.sql.DataSource

object ZioQuillContext {
  type QDataSource = Has[DataSource with Closeable]
  val dataSourceLayer: ZLayer[Any, Nothing, QDataSource] = Blocking.live >>> QDataSource.fromPrefix("zioQuillExample").orDie
}
