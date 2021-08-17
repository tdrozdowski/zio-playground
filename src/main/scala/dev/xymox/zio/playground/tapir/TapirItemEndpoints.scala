package dev.xymox.zio.playground.tapir

import dev.xymox.zio.playground.zstack.service.item.{CreateItemRequest, Item, ItemService}
import sttp.tapir.Endpoint
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

  val listingLogic: Unit => ZIO[Has[ItemService], String, Seq[Item]]        = _ => ItemService.all().mapError(_.getMessage)
  val findByIdLogic: Long => ZIO[Has[ItemService], String, Item]            = id => ItemService.get(id).mapError(_.getMessage)
  val createLogic: CreateItemRequest => ZIO[Has[ItemService], String, Item] = request => ItemService.create(request).mapError(_.getMessage)

  val baseEndpoint: Endpoint[Unit, String, Unit, Any] = endpoint.errorOut(stringBody).in("items")

  val itemsListing: ZEndpoint[Unit, String, Seq[Item]] = baseEndpoint
    .out(jsonBody[Seq[Item]])

  val itemsById: ZEndpoint[Long, String, Item] = baseEndpoint
    .in(path[Long](name = "id").description("The ID of the item you want."))
    .out(jsonBody[Item])

  val create: ZEndpoint[CreateItemRequest, String, Item] = baseEndpoint.post
    .in(jsonBody[CreateItemRequest].description("Information needed to create an Item"))
    .out(jsonBody[Item])

  val listServerEndpoint: ZServerEndpoint[Has[ItemService], Unit, String, Seq[Item]]           = itemsListing.zServerLogic[Has[ItemService]](listingLogic)
  val getServerEndpoint: ZServerEndpoint[Has[ItemService], Long, String, Item]                 = itemsById.zServerLogic[Has[ItemService]](findByIdLogic)
  val createServerEndpoint: ZServerEndpoint[Has[ItemService], CreateItemRequest, String, Item] = create.zServerLogic[Has[ItemService]](createLogic)

  val allEndpoints = List(listServerEndpoint, getServerEndpoint, createServerEndpoint)

  val itemsHttpApp: Http[Has[ItemService], Throwable, Request, Response[Has[ItemService], Throwable]] =
    ZioHttpInterpreter().toHttp(allEndpoints)
}

object ItemDocs {
  val openApi: OpenAPI                                 = OpenAPIDocsInterpreter().serverEndpointsToOpenAPI(TapirItemEndpoints.allEndpoints, "Items API", "1.0")
  val redocApp: Http[Any, Nothing, Request, UResponse] = new RedocZioHttp(title = "Items API", yaml = openApi.toYaml).endpoint
}
