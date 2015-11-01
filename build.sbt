name := """activator-akka-spray"""

version := "1.0"

scalaVersion := "2.11.6"

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "spray nightlies" at "http://nightlies.spray.io"

libraryDependencies ++= Seq(
  "com.typesafe.akka"  %% "akka-actor"       % "2.3.9",
  "com.typesafe.akka"  %% "akka-slf4j"       % "2.3.9",
  "ch.qos.logback"      % "logback-classic"  % "1.0.13",
  "io.spray"           %% "spray-can"        % "1.3.3",
  "io.spray"           %% "spray-routing"    % "1.3.3",
  "io.spray"           %% "spray-json"       % "1.3.1",
  "org.specs2"         %% "specs2"           % "2.3.11"       % "test",
  "io.spray"           %% "spray-testkit"    % "1.3.3"        % "test",
  "com.typesafe.akka"  %% "akka-testkit"     % "2.3.9"        % "test",
  "com.novocode"        % "junit-interface"  % "0.7"          % "test->default",
  "com.jsuereth"       %% "scala-arm"        % "1.4",
  "org.mockito"         % "mockito-core"     % "1.9.5"
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-language:_",
  "-target:jvm-1.7",
  "-encoding", "UTF-8"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")


fork in run := true