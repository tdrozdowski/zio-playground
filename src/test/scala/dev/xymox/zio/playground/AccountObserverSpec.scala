package dev.xymox.zio.playground

import dev.xymox.zio.playground.core.playground.AccountObserver
import dev.xymox.zio.playground.core.{AccountEvent, AccountObserver}
import zio._
import zio.console._
import zio.test._
import zio.test.Assertion._
import zio.test.environment.{TestClock, TestEnvironment}
import zio.test.mock.Expectation._
import zio.test.mock._

object AccountObserverSpec extends DefaultRunnableSpec {
  val event                           = AccountEvent("Testing Mocks!")
  val app: RIO[AccountObserver, Unit] = AccountObserver.processEvent(event)

  val mockEnv: ULayer[Console] =
    (
      MockConsole.PutStrLn(equalTo(s"Got $event"), unit) ++
        MockConsole.GetStrLn(value("42")) ++
        MockConsole.PutStrLn(equalTo("You entered: 42"))
    )

  override def spec: ZSpec[TestEnvironment, Any] = suite("processEvent")(
    testM("calls putStrLn > getStrLn > putStrLn and returns unit") {
      TestClock
      val result = app.provideLayer(mockEnv >>> AccountObserver.live)
      assertM(result)(isUnit)
    }
  )
}

// mocks
@mockable[AccountObserver.Service]
object AccountObserverMock
