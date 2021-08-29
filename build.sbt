name := "zio-playground"

version := "0.1"

scalaVersion := "3.0.1-RC2"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Sonatype OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots"
resolvers += "Moar Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

val zioVersion        = "1.0.10"
val zioPreludeVersion = "1.0.0-RC5"
val zioLoggingVersion = "0.5.11"
val zioHttpVersion    = "1.0.0.0-RC17+12-2f7aa146-SNAPSHOT"
val zioJsonVersion    = "0.1.5+42-2cb47fd2-SNAPSHOT"
val zioOpticsVersion  = "0.1.0"
val zioSchemaVersion  = "0.0.5"
val zQueryVersion     = "0.2.9"
val quillVersion      = "3.7.2.Beta1.3"
val flywayVersion     = "7.9.1"
val zioConfigVersion  = "1.0.6"
val tapirVersion      = "0.19.0-M7"
val chimneyVersion    = "0.6.1"
val sttpVersion       = "3.3.9"
val zmxVersion        = "0.0.6+12-f6d4e368-SNAPSHOT"

scalacOptions ++= Seq("-Ymacro-annotations", "-Ytasty-reader")

libraryDependencies ++= Seq(
  "dev.zio"                       %% "zio"                      % zioVersion,
  ("dev.zio"                      %% "zio-macros"               % zioVersion).cross(CrossVersion.for3Use2_13),
  "dev.zio"                       %% "zio-prelude"              % zioPreludeVersion,
  "dev.zio"                       %% "zio-json"                 % zioJsonVersion,
  "dev.zio"                       %% "zio-logging-slf4j"        % zioLoggingVersion,
  "dev.zio"                       %% "zio-config"               % zioConfigVersion,
  ("dev.zio"                      %% "zio-config-magnolia"      % zioConfigVersion).cross(CrossVersion.for3Use2_13),
  "dev.zio"                       %% "zio-config-typesafe"      % zioConfigVersion,
  "dev.zio"                       %% "zio-query"                % zQueryVersion,
  ("dev.zio"                      %% "zio-schema"               % zioSchemaVersion).cross(CrossVersion.for3Use2_13),
  ("io.github.kitlangton"         %% "zio-magic"                % "0.3.5").cross(CrossVersion.for3Use2_13),
  "dev.zio"                       %% "zio-schema"               % zioSchemaVersion,
  "dev.zio"                       %% "zio-zmx"                  % zmxVersion,
  "io.github.kitlangton"          %% "zio-magic"                % "0.3.2",
  "dev.zio"                       %% "zio-optics"               % zioOpticsVersion,
  ("io.d11"                       %% "zhttp"                    % zioHttpVersion).cross(CrossVersion.for3Use2_13),
  "io.getquill"                   %% "quill-jdbc-zio"           % quillVersion,
  "org.flywaydb"                   % "flyway-core"              % flywayVersion,
  ("com.softwaremill.sttp.tapir"  %% "tapir-zio"                % tapirVersion).cross(CrossVersion.for3Use2_13),
  ("io.scalaland"                 %% "chimney"                  % chimneyVersion).cross(CrossVersion.for3Use2_13),
  "com.softwaremill.sttp.tapir"   %% "tapir-zio"                % tapirVersion,
  "com.softwaremill.sttp.tapir"   %% "tapir-zio-http"           % tapirVersion,
  "com.softwaremill.sttp.tapir"   %% "tapir-json-zio"           % tapirVersion,
  "com.softwaremill.sttp.tapir"   %% "tapir-redoc-zio-http"     % tapirRedocZio,
  "com.softwaremill.sttp.tapir"   %% "tapir-openapi-docs"       % tapirVersion,
  "com.softwaremill.sttp.tapir"   %% "tapir-openapi-circe-yaml" % tapirVersion,
  "com.softwaremill.sttp.client3" %% "httpclient-backend-zio"   % sttpVersion,
  ("com.github.jwt-scala"         %% "jwt-core"                 % "8.0.2").cross(CrossVersion.for3Use2_13),
  "org.postgresql"                 % "postgresql"               % "42.2.8",
  "ch.qos.logback"                 % "logback-classic"          % "1.2.3",
  "net.logstash.logback"           % "logstash-logback-encoder" % "6.5",
  "dev.zio"                       %% "zio-test"                 % zioVersion % Test,
  "dev.zio"                       %% "zio-test-sbt"             % zioVersion % Test,
  "dev.zio"                       %% "zio-test-magnolia"        % zioVersion % Test
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
