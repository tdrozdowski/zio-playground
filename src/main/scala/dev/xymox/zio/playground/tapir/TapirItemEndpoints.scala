package dev.xymox.zio.playground.tapir

import dev.xymox.zio.playground.zstack.service.item.{Item, ItemService}
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.generic.auto._
import sttp.tapir.json.zio._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.redoc.ziohttp.RedocZioHttp
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir._
import zhttp.http.{Http, Request, Response, UResponse}
import zio.{Has, ZIO}

object TapirItemEndpoints {

  val listingLogic: Unit => ZIO[Has[ItemService], String, Seq[Item]] = _ => ItemService.all().mapError(_.getMessage)

  val itemsListing: ZEndpoint[Unit, String, Seq[Item]] = endpoint
    .in("items")
    .out(jsonBody[Seq[Item]])
    .errorOut(stringBody)

  val serverEndpoint: ZServerEndpoint[Has[ItemService], Unit, String, Seq[Item]] = itemsListing.zServerLogic[Has[ItemService]](listingLogic)

  val zhttpApp: Http[Has[ItemService], Throwable, Request, Response[Has[ItemService], Throwable]] =
    ZioHttpInterpreter().toHttp(serverEndpoint)
}

object ItemDocs {
  val openApi: OpenAPI                                 = OpenAPIDocsInterpreter().toOpenAPI(TapirItemEndpoints.itemsListing, "Items API", "1.0")
  val redocApp: Http[Any, Nothing, Request, UResponse] = new RedocZioHttp(title = "Items API", yaml = openApi.toYaml).endpoint
}
