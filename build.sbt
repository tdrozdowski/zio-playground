name := "zio-playground"

version := "0.1"

scalaVersion := "3.0.1"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "Sonatype OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots"

val zioVersion        = "1.0.11"
val zioPreludeVersion = "1.0.0-RC6"
val zioLoggingVersion = "0.5.11"
val zioHttpVersion    = "1.0.0.0-RC17+47-0ea2e2b7-SNAPSHOT"
val zioJsonVersion    = "0.2.0-M1"
val zioOpticsVersion  = "0.1.0"
val zioSchemaVersion  = "0.0.5"
val zQueryVersion     = "0.2.9"
val quillVersion      = "3.9.0"
val flywayVersion     = "7.9.1"
val zioConfigVersion  = "1.0.6"
val tapirVersion      = "0.19.0-M7"
val chimneyVersion    = "0.6.1"
val sttpVersion       = "3.3.9"
val zmxVersion        = "0.0.6+12-f6d4e368-SNAPSHOT"

scalacOptions ++= Seq("-Ymacro-annotations", "-Ytasty-reader")

libraryDependencies ++= Seq(
  "dev.zio"                        %% "zio"                      % zioVersion,
  ("dev.zio"                       %% "zio-macros"               % zioVersion).cross(CrossVersion.for3Use2_13),
  "dev.zio"                        %% "zio-prelude"              % zioPreludeVersion,
  "dev.zio"                        %% "zio-json"                 % zioJsonVersion,
  "dev.zio"                        %% "zio-logging-slf4j"        % zioLoggingVersion,
  "dev.zio"                        %% "zio-config"               % zioConfigVersion,
  ("dev.zio"                       %% "zio-config-magnolia"      % zioConfigVersion).cross(CrossVersion.for3Use2_13),
  "dev.zio"                        %% "zio-config-typesafe"      % zioConfigVersion,
  "dev.zio"                        %% "zio-query"                % zQueryVersion,
  ("io.github.kitlangton"          %% "zio-magic"                % "0.3.8").cross(CrossVersion.for3Use2_13),
  ("dev.zio"                       %% "zio-schema"               % zioSchemaVersion).cross(CrossVersion.for3Use2_13),
  ("dev.zio"                       %% "zio-zmx"                  % zmxVersion).cross(CrossVersion.for3Use2_13),
  "dev.zio"                        %% "zio-optics"               % zioOpticsVersion,
  ("io.d11"                        %% "zhttp"                    % zioHttpVersion).cross(CrossVersion.for3Use2_13),
  ("io.getquill"                   %% "quill-jdbc-zio"           % quillVersion).cross(CrossVersion.for3Use2_13),
  "org.flywaydb"                    % "flyway-core"              % flywayVersion,
  ("com.softwaremill.sttp.tapir"   %% "tapir-zio"                % tapirVersion).cross(CrossVersion.for3Use2_13),
  ("io.scalaland"                  %% "chimney"                  % chimneyVersion).cross(CrossVersion.for3Use2_13),
  ("com.softwaremill.sttp.tapir"   %% "tapir-zio"                % tapirVersion).cross(CrossVersion.for3Use2_13),
  ("com.softwaremill.sttp.tapir"   %% "tapir-zio-http"           % tapirVersion).cross(CrossVersion.for3Use2_13),
  ("com.softwaremill.sttp.tapir"   %% "tapir-json-zio"           % tapirVersion).cross(CrossVersion.for3Use2_13),
  ("com.softwaremill.sttp.tapir"   %% "tapir-redoc"              % tapirVersion).cross(CrossVersion.for3Use2_13),
  ("com.softwaremill.sttp.tapir"   %% "tapir-openapi-docs"       % tapirVersion).cross(CrossVersion.for3Use2_13),
  ("com.softwaremill.sttp.tapir"   %% "tapir-openapi-circe-yaml" % tapirVersion).cross(CrossVersion.for3Use2_13),
  ("com.softwaremill.sttp.client3" %% "httpclient-backend-zio"   % sttpVersion).cross(CrossVersion.for3Use2_13),
  ("com.github.jwt-scala"          %% "jwt-core"                 % "8.0.2").cross(CrossVersion.for3Use2_13),
  "org.postgresql"                  % "postgresql"               % "42.2.8",
  "ch.qos.logback"                  % "logback-classic"          % "1.2.3",
  "net.logstash.logback"            % "logstash-logback-encoder" % "6.5",
  "dev.zio"                        %% "zio-test"                 % zioVersion % Test,
  "dev.zio"                        %% "zio-test-sbt"             % zioVersion % Test,
  "dev.zio"                        %% "zio-test-magnolia"        % zioVersion % Test
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
