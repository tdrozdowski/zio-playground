package dev.xymox.zio.playground.prelude

import zio.prelude.fx.ZPure
import zio._
import zio.console._
import zio.prelude.{EState, State}

import java.io.IOException

object ZPureExamples extends App {
  case class AccountEnvironment(interestRate: Double)

  val interestRate: ZPure[Nothing, Unit, Unit, AccountEnvironment, Nothing, Double] = ZPure.access(_.interestRate)

  def computeSimpleInterest(balance: Double, days: Int, interestRate: Double): ZPure[Nothing, Unit, Unit, Any, Nothing, Double] =
    ZPure.succeed(balance * days / 365 * interestRate)

  def accruedInterest(balance: Double, days: Int): ZPure[Nothing, Unit, Unit, AccountEnvironment, Nothing, Double] =
    ZPure.accessM(r => computeSimpleInterest(balance, days, r.interestRate))

  val interestComputation: ZPure[Nothing, Unit, Unit, Any, Nothing, Double] = accruedInterest(100000, 30).provide(AccountEnvironment(0.05))

  val interestDue: Double = interestComputation.run

  val program: ZIO[Console, IOException, Unit] = putStrLn(s"Interest due: $interestDue")

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.provideLayer(ZEnv.live).exitCode

}

object ZPureStateExample extends App {
  case class AccountState(balance: Double, open: Boolean)

  sealed trait AccountError
  case object InsufficientFunds extends AccountError

  def withdraw(amount: Int): ZPure[Nothing, AccountState, AccountState, Any, AccountError, Unit] =
    for {
      state <- ZPure.get[AccountState]
      _     <- if (amount > state.balance) ZPure.fail(InsufficientFunds) else ZPure.set(AccountState(state.balance - amount, state.open))
    } yield ()

  def decrementBalance(amount: Int): EState[AccountState, AccountError, Unit] =
    ZPure.update(state => AccountState(state.balance - amount, state.open))

  def safeWithdraw(amount: Int): EState[AccountState, AccountError, Unit] =
    for {
      state <- ZPure.get[AccountState]
      _     <- if (amount > state.balance) ZPure.fail(InsufficientFunds) else decrementBalance(amount)
    } yield ()

  val withdrawalComputation: ZPure[Nothing, Any, AccountState, Any, AccountError, Unit] =
    safeWithdraw(10).provideState(AccountState(100, true))

  val overdraft =
    safeWithdraw(500).provideState(AccountState(100, true))

  val updateAccountState: Either[AccountError, AccountState] =
    (withdrawalComputation *> ZPure.get).runEither

  val attemptOverdraftState: Either[AccountError, AccountState] =
    (overdraft *> ZPure.get).runEither

  val program: ZIO[Console, Object, Unit] =
    for {
      _                <- putStrLn("Running withdrawal...")
      results          <- ZIO.fromEither(updateAccountState)
      _                <- putStrLn(s"Results: $results")
      _                <- putStrLn("Attempt overdraft...")
      overdraftResults <- ZIO.fromEither(attemptOverdraftState)
      _                <- putStrLn(s"Overdraft results: $overdraftResults")
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.provideLayer(ZEnv.live).exitCode
}
