organization := "todomvc"
name         := "default"
version      := "0.1-SNAPSHOT"

scalaVersion := "2.11.7"

lazy val bulletin      = "com.davegurnell"    %% "bulletin"          % "0.2.0"
lazy val circeCore     = "io.circe"           %% "circe-core"        % "0.3.0"
lazy val circeGeneric  = "io.circe"           %% "circe-generic"     % "0.3.0"
lazy val doobieCore    = "org.tpolecat"       %% "doobie-core"       % "0.2.3"
lazy val doobieH2      = "org.tpolecat"       %% "doobie-contrib-h2" % "0.2.3"
lazy val finchCore     = "com.github.finagle" %% "finch-core"        % "0.10.0"
lazy val finchCirce    = "com.github.finagle" %% "finch-circe"       % "0.10.0"
lazy val twitterServer = "com.twitter"        %% "twitter-server"    % "1.18.0"

resolvers += "TM" at "http://maven.twttr.com"

libraryDependencies ++= Seq(
  bulletin,
  circeCore,
  circeGeneric,
  doobieCore,
  doobieH2,
  finchCore,
  finchCirce,
  twitterServer
)
