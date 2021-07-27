package dev.xymox.zio.playground.zmx.server

import zhttp.http._
import zio.{Has, ZIO}
import zio.zmx.MetricSnapshot.{Json, Prometheus}
import zio.zmx.prometheus.PrometheusClient
import zio.zmx.statsd.StatsdClient

object InstrumentedEndpoints {

  val index: Http[Any, Nothing, Request, UResponse] = Http.collect[Request] { case Method.GET -> Root =>
    Response.text(
      """<html>
        |<title>Simple Server</title>
        |<body>
        |<p><a href="/metrics">Metrics</a></p>
        |<p><a href="/json">Json</a></p>
        |</body
        |</html>""".stripMargin
    )
  }

  val metrics: Http[Has[PrometheusClient], Nothing, Request, UResponse] = Http.collectM[Request] { case Method.GET -> Root / "metrics" =>
    PrometheusClient.snapshot.map { case Prometheus(value) =>
      Response.text(value)
    }
  }

  val statsd: Http[Has[StatsdClient], Nothing, Request, UResponse] = Http.collectM[Request] { case Method.GET -> Root / "json" =>
    StatsdClient.snapshot.map { case Json(value) => Response.jsonString(value) }
  }
}
