package dev.xymox.zio.playground.tapir

import dev.xymox.zio.playground.zstack.service.item.{Item, ItemService}
import sttp.tapir._
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.generic.auto._
import sttp.tapir.json.zio._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.redoc.ziohttp.RedocZioHttp
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zhttp.http.{Http, Request, Response, UResponse}
import zio.{Has, URIO, ZIO}

object TapirItemEndpoints {

  // TODO - figure out how to handle errors here properly so it all ties together
  val itemsListing: Endpoint[Unit, Unit, Seq[Item], Any] =
    endpoint.in("items").out(jsonBody[Seq[Item]])

  val wrapService: () => URIO[Has[ItemService], Either[Throwable, Seq[Item]]] = () => ItemService.all.either

  val zhttpApp: Http[Any, Throwable, Request, Response[Any, Throwable]] =
    ZioHttpInterpreter().toHttp(itemsListing)(wrapService)
}

object ItemDocs {
  val openApi: OpenAPI                                 = OpenAPIDocsInterpreter().toOpenAPI(TapirItemEndpoints.itemsListing, "Items API", "1.0")
  val redocApp: Http[Any, Nothing, Request, UResponse] = new RedocZioHttp(title = "Items API", yaml = openApi.toYaml).endpoint
}
