# Typelevel TodoMVC

Sandbox repo containing a Todo list app implemented using Typelevel libraries wherever possible.

[![Build Status](https://travis-ci.org/davegurnell/typelevel-todomvc.svg?branch=develop)](https://travis-ci.org/davegurnell/typelevel-todomvc)

Server code Copyright 2016 Dave Gurnell. Licensed [Apache 2][apache-license].

Client code adapted from the [Diode TodoMVC example][diode-todomvc]. Copyright 2015 Otto Chrons. Licensed [MIT][mit-license].

## Running

The app is implemented in two parts:

 - a server written using [Finch][finch], [Circe][circe], [Doobie][doobie], and [H2][h2];
 - a client written using [ScalaJS][scalajs], [Diode][diode], and [ScalaJS-React][scalajs-react].

To fire everything up:

1. Open two different terminals in the root directory of the project;

2. In one terminal type the following to start the API server running on `localhost:8080`:

   ~~~ bash
   sbt 'todoJVM/run'
   ~~~

3. In the other terminal type the following to build the client:

   ~~~ bash
   sbt '~todoJS/fastOptJS'
   ~~~

4. The build for the client uses Li Haoyi's [ScalaJS Workbench][scalajs-workbench],
   which creates a second web server to serve the ScalaJS UI.
   Open the following web address in your browser to see the app:

   [http://localhost:12345/js/target/scala-2.11/classes/index.html]()

[diode-todomvc]: https://github.com/ochrons/diode/tree/master/examples/todomvc
[apache-license]: http://www.apache.org/licenses/LICENSE-2.0
[mit-license]: https://opensource.org/licenses/MIT
[finch]: https://github.com/finagle/finch
[circe]: https://github.com/travisbrown/circe
[doobie]: https://github.com/tpolecat/doobie
[h2]: http://www.h2database.com
[scalajs]: http://www.scala-js.org
[diode]: https://github.com/ochrons/diode
[scalajs-react]: https://github.com/japgolly/scalajs-react
[scalajs-workbench]: https://github.com/lihaoyi/workbench