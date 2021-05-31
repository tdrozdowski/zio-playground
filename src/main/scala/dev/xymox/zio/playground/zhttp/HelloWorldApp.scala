package dev.xymox.zio.playground.zhttp

import zhttp.http._
import zio.ZIO
import zio.json._

object HelloWorldApp {

  val requireAuth: Http[Any, IllegalArgumentException, Any, UResponse] = Http.collectM {
    case Request(_, data) if data.headers.exists(_.name == "Authorization") => ZIO.succeed(Response.text("Some Valid JWT Claim"))
    case _                                                                  => ZIO.fail(new IllegalArgumentException("Missing Validation"))
  }

  val app: Http[Any, Nothing, Request, UResponse] = Http.collectM { case req @ Method.GET -> Root / "hello" =>
    for {
      params <- ZIO.succeed(req.endpoint._2.queryParams)
    } yield Response.text(s"Hello World! Params:'$params'")
  }

  val jsonApp: Http[Any, Nothing, Request, UResponse] = Http.collectM { case Method.GET -> Root / "jsonHello" =>
    ZIO.succeed(Response.jsonString(Message("Hello, JSON World!").toJson))
  }
}

case class Message(value: String)

object Message {
  implicit val codec: JsonCodec[Message] = DeriveJsonCodec.gen[Message]
}
