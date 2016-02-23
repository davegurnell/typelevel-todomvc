organization in ThisBuild := "com.davegurnell"
name         in ThisBuild := "todomvc"
version      in ThisBuild := "0.1-SNAPSHOT"
scalaVersion in ThisBuild := "2.11.7"

lazy val circeCore     = "io.circe"           %% "circe-core"        % "0.3.0"
lazy val circeGeneric  = "io.circe"           %% "circe-generic"     % "0.3.0"
lazy val doobieCore    = "org.tpolecat"       %% "doobie-core"       % "0.2.3"
lazy val doobieH2      = "org.tpolecat"       %% "doobie-contrib-h2" % "0.2.3"
lazy val finchCore     = "com.github.finagle" %% "finch-core"        % "0.10.0"
lazy val finchCirce    = "com.github.finagle" %% "finch-circe"       % "0.10.0"
lazy val specs2        = "org.specs2"         %% "specs2-core"       % "3.7"
lazy val twitterServer = "com.twitter"        %% "twitter-server"    % "1.18.0"

lazy val diode         = "me.chrons"         %%% "diode"             % "0.5.0"

lazy val todo = crossProject.in(file("."))
  .settings(
    resolvers += "Twitter Maven" at "http://maven.twttr.com",
    libraryDependencies ++= Seq(
      circeCore,
      circeGeneric
    )
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      doobieCore,
      doobieH2,
      finchCore,
      finchCirce,
      specs2 % Test,
      twitterServer
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      diode
    )
  )

lazy val todoJVM = todo.jvm
lazy val todoJS  = todo.js

lazy val root = project.in(file("."))
  .aggregate(todoJVM, todoJS)
