package dev.xymox.zio.playground.quill

import io.getquill.context.ZioJdbc.DataSourceLayer
import zio.blocking.Blocking
import zio.{Has, ZLayer}

import javax.sql.DataSource

object ZioQuillContext {
  val dataSourceLayer: ZLayer[Any, Nothing, Has[DataSource]] = Blocking.live >>> DataSourceLayer.fromPrefix("zioQuillExample").orDie
}
