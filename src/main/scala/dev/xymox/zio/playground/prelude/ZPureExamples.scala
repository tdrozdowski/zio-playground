package dev.xymox.zio.playground.prelude

import zio.prelude.fx.ZPure
import zio._
import zio.console._
import zio.prelude.{EState, State}

import java.io.IOException

/** ZPure is a 'pure' function - no side effects!
  *
  *  A ZPure is defined as: ZPure[+W, -S1, +S2, -R, +E, +A]
  *  Which really means a function like: (R, S1) => (Chunk[W], Either[E, (S2, A)])
  *
  *  (From the forthcoming ZIO Prelude documentation...)
  * The `ZPure` data type models four "capabilities" that a computation can have in addition to just producing a value of type `A`:
  *
  * - **Errors** - A `ZPure` computation can fail with an error of type `E` similar to an `Either`.
  * - **Context** - A `ZPure` computation can require some environment of type `R` similar to a `Reader` data type.
  * - **State** - A `ZPure` computation can update a state `S1` to a new state `S2` similar to a `State` data type.
  * - **Logging** - A `ZPure` computation can maintain a log of type `W` similar to a `Writer` data type.
  */
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

  val overdraft: ZPure[Nothing, Any, AccountState, Any, AccountError, Unit] =
    safeWithdraw(500).provideState(AccountState(100, true))

  val updateAccountState: Either[AccountError, AccountState] =
    (withdrawalComputation *> ZPure.get).runEither

  val attemptOverdraftState: Either[AccountError, AccountState] =
    (overdraft *> ZPure.get).runEither

  def withdrawLog(amount: Int): ZPure[String, AccountState, AccountState, Any, AccountError, Unit] =
    ZPure.log(s"Attemping to withdraw $amount") *> safeWithdraw(amount) <* ZPure.log(s"Withdrew amount: $amount")

  val withdrawComputationLog: ZPure[String, AccountState, AccountState, Any, AccountError, Unit] = withdrawLog(10)

  val log: Chunk[String] = withdrawComputationLog.runAll(AccountState(100, true))._1

  val program: ZIO[Console, Object, Unit] =
    for {
      _                <- putStrLn("Running withdrawal...")
      results          <- ZIO.fromEither(updateAccountState)
      _                <- putStrLn(s"Results: $results")
      _                <- putStrLn(s"Withdraw log: $log")
      _                <- putStrLn("Attempt overdraft...")
      overdraftResults <- ZIO.fromEither(attemptOverdraftState)
      _                <- putStrLn(s"Overdraft results: $overdraftResults")
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.provideLayer(ZEnv.live).exitCode
}
