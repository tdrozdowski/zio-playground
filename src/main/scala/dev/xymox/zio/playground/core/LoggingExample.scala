package dev.xymox.zio.playground.core

import zio._
import zio.clock.Clock
import zio.duration._
import zio.logging._
import zio.logging.slf4j._

import java.util.UUID

object Simple extends zio.App {

  val env =
    Logging.console(
      logLevel = LogLevel.Info,
      format = LogFormat.ColoredLogFormat()
    ) >>> Logging.withRootLoggerName("my-component")

  override def run(args: List[String]) =
    log.info("Hello from ZIO logger").provideCustomLayer(env).exitCode
}

object LogLevelAndLoggerName extends zio.App {

  val env =
    Logging.consoleErr()

  override def run(args: List[String]) =
    log
      .locally(LogAnnotation.Name("logger-name-here" :: Nil)) {
        log.info("Hello from ZIO logger")
      }
      .provideCustomLayer(env)
      .exitCode
}

object Slf4jAndCorrelationId extends zio.App {
  val logFormat = "[correlation-id = %s] %s"

  val env =
    Slf4jLogger.make { (context, message) =>
      val correlationId = LogAnnotation.CorrelationId.render(
        context.get(LogAnnotation.CorrelationId)
      )
      logFormat.format(correlationId, message)
    }

  def generateCorrelationId: Option[UUID] =
    Some(UUID.randomUUID())

  override def run(args: List[String]) =
    (for {
      fiber <- log.locally(_.annotate(LogAnnotation.CorrelationId, generateCorrelationId))(ZIO.unit).fork
      _     <- log.info("info message without correlation id")
      _     <- fiber.join
      _     <- log.locally(_.annotate(LogAnnotation.CorrelationId, generateCorrelationId)) {
        log.info("info message with correlation id") *>
          log.throwable("another info message with correlation id", new RuntimeException("BOOOOOM")).fork
      }
    } yield ExitCode.success).provideLayer(env)
}

object Slf4jMdc extends zio.App {

  val userId = LogAnnotation[UUID](
    name = "user-id",
    initialValue = UUID.fromString("0-0-0-0-0"),
    combine = (_, newValue) => newValue,
    render = _.toString
  )

  val logLayer = Slf4jLogger.makeWithAnnotationsAsMdc(List(userId))
  val users    = List.fill(2)(UUID.randomUUID())

  override def run(args: List[String]) =
    (for {
      _             <- log.info("Start...")
      correlationId <- UIO.some(UUID.randomUUID())
      _             <- ZIO.foreachPar_(users) { uId =>
        log.locally(_.annotate(userId, uId).annotate(LogAnnotation.CorrelationId, correlationId)) {
          log.info("Starting operation") *>
            ZIO.sleep(500.millis) *>
            log.info("Stopping operation")
        }
      }
    } yield ExitCode.success).provideSomeLayer[Clock](logLayer)
}
