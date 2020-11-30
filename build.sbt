name := "zio-playground"

version := "0.1"

scalaVersion := "2.13.4"

val zioVersion = "1.0.3"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"      % zioVersion,
  "dev.zio" %% "zio-test" % zioVersion
)
