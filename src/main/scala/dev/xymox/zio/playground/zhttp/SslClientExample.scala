package dev.xymox.zio.playground.zhttp

import zhttp.http.HttpData.CompleteData
import zhttp.http.{Header, HttpError, Method, Request, URL}
import zhttp.service.{ChannelFactory, Client, EventLoopGroup}
import zio._
import zio.console._

object SslClientExample extends App {
  val env: TaskLayer[ChannelFactory with EventLoopGroup] = ChannelFactory.auto ++ EventLoopGroup.auto()
  val hostHeader: Header                                 = Header.host("p2p.dev.nosidelines.io")
  val testDriveLocationsUrl: Either[HttpError, URL]      = URL.fromString("https://p2p.dev.nosidelines.io/api/v1/testDriveLocations")

  val getTestDriveLocations: ZIO[EventLoopGroup with ChannelFactory, Throwable, String] =
    for {
      url         <- ZIO.fromEither(testDriveLocationsUrl)
      results     <- Client.request(Request(endpoint = Method.GET -> url, headers = List(hostHeader)))
      locationsStr = results.content match {
        case CompleteData(data) => data.map(_.toChar).mkString
        case _                  => "<you shouldn't see this>"
      }
    } yield locationsStr

  val program =
    for {
      locations <- getTestDriveLocations
      _         <- putStrLn(s"Test drive locations: $locations")
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.provideLayer(ZEnv.live ++ env).exitCode
}
