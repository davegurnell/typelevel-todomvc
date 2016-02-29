package todomvc.server

import com.twitter.finagle.Http
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import todomvc.core._

object Main extends TwitterServer {
  import AsyncImplicits._

  def main(): Unit = {
    val api = Await.result(DoobieTodoDatabase.create.map(new TodoApi(_)).toFuture)
    val server = Http.server.serve(":8080", api.service)
    onExit { server.close() }
    Await.ready(server)
  }
}
