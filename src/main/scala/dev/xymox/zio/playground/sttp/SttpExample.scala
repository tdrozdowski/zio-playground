package dev.xymox.zio.playground.sttp

import sttp.client3._
import sttp.client3.httpclient.zio._
import sttp.model.Uri
import zio._
import zio.console._

object SttpExample extends App {

  val getTestDriveLocations: ZIO[SttpClient, Serializable, String] =
    for {
      request  <- ZIO.effect(basicRequest.get(Uri(scheme = "https", host = "p2p.nosidelines.io", path = Seq("api", "v1", "testDriveLocations"))))
      response <- send(request)
      results  <- ZIO.fromEither(response.body)
    } yield results

  val program: ZIO[Console with SttpClient, Serializable, Unit] =
    for {
      locationJson <- getTestDriveLocations
      _            <- putStrLn(s"Response:\n$locationJson")
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.provideLayer(ZEnv.live ++ HttpClientZioBackend.layer()).exitCode
}
