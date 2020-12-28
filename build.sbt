name := "zio-playground"

version := "0.1"

scalaVersion := "2.13.4"

val zioVersion        = "1.0.3"
val zioLoggingVersion = "0.5.4"

scalacOptions += "-Ymacro-annotations"

libraryDependencies ++= Seq(
  "dev.zio"             %% "zio"                      % zioVersion,
  "dev.zio"             %% "zio-macros"               % zioVersion,
  "dev.zio"             %% "zio-json"                 % "0.0.1",
  "dev.zio"             %% "zio-logging-slf4j"        % zioLoggingVersion,
  "ch.qos.logback"       % "logback-classic"          % "1.2.3",
  "net.logstash.logback" % "logstash-logback-encoder" % "6.5",
  "dev.zio"             %% "zio-test"                 % zioVersion % Test,
  "dev.zio"             %% "zio-test-sbt"             % zioVersion % Test,
  "dev.zio"             %% "zio-test-magnolia"        % zioVersion % Test
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
