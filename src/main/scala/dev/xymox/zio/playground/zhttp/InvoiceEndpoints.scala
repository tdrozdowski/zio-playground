package dev.xymox.zio.playground.zhttp

import zhttp.http._

object InvoiceEndpoints {

  val invoiceRoutes: Http[Any, Nothing, Request, UResponse] = Http.route[Request] {
    case req @ Method.GET -> Root / "api" / "v1" =>
      val root = req.endpoint._2.path
      Http.collect[Request] { case Method.GET -> root / "invoices" =>
        Response.jsonString("""{"message": "aMessage"}""")
      }
    case req: Request                            =>
      val root = req.endpoint._2
      Http.succeed(Response.fromHttpError(HttpError.InternalServerError(s"Wait - wut: ${root.path}")))
  }
}
