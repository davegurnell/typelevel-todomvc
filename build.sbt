organization := "todomvc"
name         := "default"
version      := "0.1-SNAPSHOT"
scalaVersion := "2.11.7"

lazy val bulletin      = "com.davegurnell"    %% "bulletin"       % "0.2.0"
lazy val finchCore     = "com.github.finagle" %% "finch-core"     % "0.10.0"
lazy val finchCirce    = "com.github.finagle" %% "finch-circe"    % "0.10.0"
lazy val twitterServer = "com.twitter"        %% "twitter-server" % "1.18.0"
lazy val circeCore     = "io.circe"           %% "circe-core"     % "0.3.0"
lazy val circeGeneric  = "io.circe"           %% "circe-generic"  % "0.3.0"

resolvers += "TM" at "http://maven.twttr.com"

libraryDependencies ++= Seq(
  bulletin,
  circeCore,
  circeGeneric,
  finchCore,
  finchCirce,
  twitterServer
)
