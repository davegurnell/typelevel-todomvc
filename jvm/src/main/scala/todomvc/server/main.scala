package todomvc.server

import com.twitter.finagle.Http
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import todomvc.core._

object Main extends TwitterServer {
  import AsyncImplicits._

  val db  = new DoobieTodoDatabase()
  val api = new TodoApi(db)

  Await.ready {
    db.init().toFuture map { unit =>
      val server = Http.server.serve(":8080", api.service)
      onExit { server.close() }
      Await.ready(server)
    }
  }
}
