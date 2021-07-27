package dev.xymox.zio.playground.zstack.zmx

import zio.zmx.metrics.MetricAspect

object ServiceMetrics {
  val createCountAll: MetricAspect[Any] = MetricAspect.count("itemCreate")
  val listCountAll: MetricAspect[Any]   = MetricAspect.count("itemList")
}
