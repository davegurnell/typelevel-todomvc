organization in ThisBuild := "com.davegurnell"
name         in ThisBuild := "todomvc"
version      in ThisBuild := "0.1-SNAPSHOT"
scalaVersion in ThisBuild := "2.11.7"

lazy val todo = crossProject.in(file("."))
  .settings(
    resolvers += "Twitter Maven" at "http://maven.twttr.com"
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "io.circe"           %% "circe-core"        % "0.3.0",
      "io.circe"           %% "circe-generic"     % "0.3.0",
      "org.tpolecat"       %% "doobie-core"       % "0.2.3",
      "org.tpolecat"       %% "doobie-contrib-h2" % "0.2.3",
      "com.github.finagle" %% "finch-core"        % "0.10.0",
      "com.github.finagle" %% "finch-circe"       % "0.10.0",
      "org.specs2"         %% "specs2-core"       % "3.7"     % Test,
      "com.twitter"        %% "twitter-server"    % "1.18.0"
    )
  )
  .jsSettings(workbenchSettings : _*)
  .jsSettings(
    bootSnippet      := "TodoMVCApp().main();",
    testFrameworks   += new TestFramework("utest.runner.Framework"),
    emitSourceMaps   := true,
    persistLauncher  := true,
    libraryDependencies ++= Seq(
      "io.circe"                          %%% "circe-core"     % "0.3.0",
      "io.circe"                          %%% "circe-generic"  % "0.3.0",
      "io.circe"                          %%% "circe-parser"   % "0.3.0",
      "org.scala-js"                      %%% "scalajs-dom"    % "0.9.0",
      "com.github.japgolly.scalajs-react" %%% "core"           % "0.10.4",
      "com.github.japgolly.scalajs-react" %%% "extra"          % "0.10.4",
      "me.chrons"                         %%% "diode"          % "0.5.0",
      "me.chrons"                         %%% "diode-devtools" % "0.5.0",
      "me.chrons"                         %%% "diode-react"    % "0.5.0",
      "me.chrons"                         %%% "boopickle"      % "1.1.2"
    ),
    jsDependencies ++= Seq(
      "org.webjars.bower" % "react" % "0.14.7" / "react-with-addons.js" commonJSName "React"    minified "react-with-addons.min.js",
      "org.webjars.bower" % "react" % "0.14.7" / "react-dom.js"         commonJSName "ReactDOM" minified "react-dom.min.js" dependsOn "react-with-addons.js"
    )
  )

lazy val todoJVM = todo.jvm
lazy val todoJS  = todo.js

lazy val root = project.in(file("."))
  .aggregate(todoJVM, todoJS)
