package dev.xymox.zio.playground.tapir

import dev.xymox.zio.playground.zstack.service.item.{CreateItemRequest, Item, ItemService}
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.Endpoint
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.generic.auto._
import sttp.tapir.json.zio._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.redoc.Redoc
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir._
import zhttp.http.{Http, Request, Response}
import zio.{Has, RIO, Task, ZIO}

object TapirItemEndpoints {
  // TODO - figure out auth; add CORS; how to group endpoints for redoc/openapi
  val listingLogic: Unit => ZIO[Has[ItemService], String, Seq[Item]]        = _ => ItemService.all().mapError(_.getMessage)
  val findByIdLogic: Long => ZIO[Has[ItemService], String, Item]            = id => ItemService.get(id).mapError(_.getMessage)
  val createLogic: CreateItemRequest => ZIO[Has[ItemService], String, Item] = request => ItemService.create(request).mapError(_.getMessage)

  val baseEndpoint: Endpoint[Unit, String, Unit, Any] =
    endpoint.tags(List("Items")).errorOut(stringBody).in("items")

  val itemsListing: ZEndpoint[Unit, String, Seq[Item]] = baseEndpoint
    .name("List Items")
    .summary("List all Items in the shop.")
    .description("Returns a full list of all items in the shop.  No paging is currently implemented.")
    .out(jsonBody[Seq[Item]].description("All the Items"))

  val itemsById: ZEndpoint[Long, String, Item] = baseEndpoint
    .name("Get Item")
    .summary("Get a Specific Item")
    .description("Given an ID for an Item, return it.")
    .in(path[Long](name = "id").description("The ID of the item you want."))
    .out(jsonBody[Item].description("The Item"))

  val create: ZEndpoint[CreateItemRequest, String, Item] = baseEndpoint.post
    .name("Create Item")
    .summary("Create a New Item")
    .description("Create a new Item in the database")
    .in(jsonBody[CreateItemRequest].description("Information needed to create an Item"))
    .out(jsonBody[Item].description("The Item"))

  val listServerEndpoint: ZServerEndpoint[Has[ItemService], Unit, String, Seq[Item]]           = itemsListing.zServerLogic[Has[ItemService]](listingLogic)
  val getServerEndpoint: ZServerEndpoint[Has[ItemService], Long, String, Item]                 = itemsById.zServerLogic[Has[ItemService]](findByIdLogic)
  val createServerEndpoint: ZServerEndpoint[Has[ItemService], CreateItemRequest, String, Item] = create.zServerLogic[Has[ItemService]](createLogic)

  // NOTE - because Tapir ZServerEndpoint gives us an R type with ZioStreams with WebSockets and the zio-http interpreter
  // doesn't (yet) support the WebSockets capability - we have to cast this down and use the EffectType type alias to help us
  // mash this into shape.
  type EffectType[A] = RIO[Has[ItemService], A]

  val allEndpoints =
    List(listServerEndpoint, getServerEndpoint, createServerEndpoint)
      .asInstanceOf[List[ServerEndpoint[_, _, _, ZioStreams, EffectType]]]

  val itemsHttpApp: Http[Has[ItemService], Throwable, Request, Response[Has[ItemService], Throwable]] =
    ZioHttpInterpreter().toHttp(allEndpoints)
}

object ItemDocs {
  val openApi: OpenAPI                                                  = OpenAPIDocsInterpreter().serverEndpointsToOpenAPI(TapirItemEndpoints.allEndpoints, "Items API", "1.0")
  val redocApp: Http[Any, Throwable, Request, Response[Any, Throwable]] = ZioHttpInterpreter().toHttp(Redoc[Task](title = "Items API", yaml = openApi.toYaml))
}
