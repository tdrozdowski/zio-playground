package dev.xymox.zio.playground

import zio._
import zio.console._
import zio.random._
import zio.duration._
import zio.clock._

object Main extends App {

  val mySchedule: Schedule[Any, Any, Long] = Schedule.spaced(2.seconds) <* Schedule.recurs(2)

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    for {
      r <- retryWithCount(doOrFail, mySchedule)
      (result, count) = r
      _ <- zio.console.putStrLn(s"result is: $result, final count: $count")
      result <- ZIO.fromEither(result).mapError(v => new IllegalArgumentException(v)).orDie
      _ <- console.putStrLn(s"The successful result is: $result")
    } yield ExitCode.success

  val doOrFail: ZIO[Random with Console, String, String] =
    putStrLn("trying....") *> zio.random.nextDouble.flatMap(d => if (d < 0.5) ZIO.fail("failure!") else ZIO.succeed("success!"))

  def retryWithCount[R, E, A, R1 <: R, A1](zio: ZIO[R, E, A], schedule: Schedule[R1, E, A1]): ZIO[R1 with Clock, Nothing, (Either[E, A], Long)] = {
    for {
      count <- Ref.make[Long](0)
      update = count.update(_ + 1)
      result <- zio.tapBoth(_ => update, _ => update).retry(schedule).either
      finalCount <- count.get
    } yield (result, finalCount)
  }
}
