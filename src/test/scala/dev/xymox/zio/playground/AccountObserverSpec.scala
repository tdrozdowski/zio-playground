package dev.xymox.zio.playground

import zio._
import zio.console.Console
import zio.test._
import zio.test.Assertion._
import zio.test.environment.{TestClock, TestEnvironment}
import zio.test.mock.Expectation._
import zio.test.mock._
import zio.test.{suite, DefaultRunnableSpec, ZSpec}

object AccountObserverSpec extends DefaultRunnableSpec {
  val event                            = AccountEvent("Testing Mocks!")
  val app: URIO[AccountObserver, Unit] = AccountObserver.processEvent(event)

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
