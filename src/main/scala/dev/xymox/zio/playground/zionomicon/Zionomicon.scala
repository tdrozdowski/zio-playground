package dev.xymox.zio.playground.zionomicon

import java.io.{File, PrintWriter}

import Zionomicon.readFile
import zio._
import zio.console.Console

import scala.io.{Source, StdIn}
import scala.util.Random

object Zionomicon extends App {

  def readFile(file: String): Task[String] =
    for {
      source <- ZIO.effect(Source.fromFile(file))
    } yield try source.getLines.mkString("\n")
    finally source.close()

  def writeFile(file: String, text: String): Task[Unit] =
    for {
      pw <- ZIO.effect(new PrintWriter(new File(file)))
    } yield try pw.write(text)
    finally pw.close()

  def copyFile(source: String, dest: String): Task[Unit] =
    for {
      contents <- readFile(source)
      _ <- writeFile(dest, contents)
    } yield ()

  def printLine(line: String): Task[Unit] = ZIO.effect(println(line))
  val readLine = ZIO.effect(StdIn.readLine())

  val printName =
    for {
      _ <- printLine("What is your name?")
      name <- readLine
      _ <- printLine(s"Hello, ${name}!")
    } yield ()

  val guessRandom: ZIO[Any, Throwable, Unit] =
    for {
      random <- ZIO.effect(Random.nextInt(3) + 1)
      _ <- printLine("Guess a number from 1 to 3:")
      guess <- readLine
      _ <- printLine(s"You guessed right!  The number was $random").when(guess == random.toString)
      _ <- printLine(s"You guessed wrong, the number was $random").when(guess != random.toString)
    } yield ()

  def printFile(file: String): Task[Unit] =
    for {
      lines <- readFile(file)
      _ <- ZIO.effect(println(s"file lines:\n$lines"))
    } yield ()

  def run(args: List[String]): URIO[Any with Console, ExitCode] =
    guessRandom.exitCode
  //printFile("build.sbt").exitCode
}

object Cat extends App {

  def readFile(file: String): Task[String] =
    for {
      source <- ZIO.effect(Source.fromFile(file))
    } yield try source.getLines.mkString("\n")
    finally source.close()

  def printFile(file: String): Task[Unit] =
    for {
      lines <- readFile(file)
      _ <- ZIO.effect(println(s"----\n$file:\n----\n$lines"))
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    ZIO.foreach(args)(printFile).exitCode

}
