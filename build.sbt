name := "zio-playground"

version := "0.1"

scalaVersion := "2.13.4"

val zioVersion = "1.0.3"

scalacOptions += "-Ymacro-annotations"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"        % zioVersion,
  "dev.zio" %% "zio-macros" % zioVersion,
  "dev.zio" %% "zio-json"   % "0.0.1",
  "dev.zio" %% "zio-test"   % zioVersion
)
