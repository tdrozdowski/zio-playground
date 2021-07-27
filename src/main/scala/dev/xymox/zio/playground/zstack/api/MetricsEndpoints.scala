package dev.xymox.zio.playground.zstack.api

import zhttp.http._
import zio.Has
import zio.zmx.MetricSnapshot.Prometheus
import zio.zmx.prometheus.PrometheusClient

object MetricsEndpoints {

  val metrics: Http[Has[PrometheusClient], Nothing, Request, UResponse] = Http.collectM[Request] { case Method.GET -> Root / "metrics" =>
    PrometheusClient.snapshot.map { case Prometheus(value) =>
      Response.text(value)
    }
  }
}
