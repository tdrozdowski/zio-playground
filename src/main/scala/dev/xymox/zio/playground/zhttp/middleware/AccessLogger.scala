//package dev.xymox.zio.playground.zhttp.middleware
//
//import zhttp.http.Response.HttpResponse
//import zhttp.http.{Http, Request}
//import zio.logging.{LogAnnotation, Logging}
//
//import java.time.Instant
//
//case object AccessLogger {
//
//  // taken from Discord: @gavares
//  def accessLogger[R, E](next: Http[R, E, Request, HttpResponse[R, E]]): Http[Logging with R, E, Request, HttpResponse[R, E]] = {
//    Http.flatten {
//      Http.fromFunction[Request] { req =>
//        val path   = req.url.path
//        val method = req.method
//        val reqId  = req.getHeader(RequestIdHeader).getOrElse(genOpcReqId) // TODO - sort this out
//        val start  = Instant.now().toEpochMilli
//
//        next >>> Http.fromEffectFunction[HttpResponse[R, E]] { resp => // TODO - try foldM here instead
//          val duration = Instant.now().toEpochMilli - start
//          Logging
//            .locally(LogAnnotation.Name("access-log" :: Nil)) {
//              Logging.info(s"path: $path, method: $method, reqId: $reqId, status: ${resp.status},timeMillis: $duration")
//            }
//            .as(resp)
//        }
//      }
//    }
//  }
//}
