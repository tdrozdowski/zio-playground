package dev.xymox.zio.playground.zstack.zmx

import zio.Chunk
import zio.zmx.metrics.MetricAspect

object ServiceMetrics {
  val createCountAll: MetricAspect[Any] = MetricAspect.count("api_item", ("operation", "create"))
  val listCountAll: MetricAspect[Any]   = MetricAspect.count("api_item", ("operation", "list"))

  val durationsList: MetricAspect[Any] =
    MetricAspect.observeDurations("api_items_seconds", boundaries = Chunk(0.0, 50.0, 95.5), ("operation", "list"))(_.toMillis)
}
