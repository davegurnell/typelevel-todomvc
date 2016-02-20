package todomvc

import bulletin._
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import java.util.UUID
import com.twitter.util.Future
import shapeless._

object Api extends TwitterServer {
  import AsyncImplicits._

  lazy val listEndpoint = get("todo") {
    TodoDb.list().toFuture map (Ok(_))
  }

  val createEndpoint = post("todo" :: body.as[TodoUpdate]) { (update: TodoUpdate) =>
    TodoDb.save(Todo.create merge update).toFuture map (Ok(_))
  }

  val readEndpoint = get("todo" / uuid) { (id: UUID) =>
    TodoDb.find(id).toFuture map {
      case Some(todo) => Ok(todo)
      case None       => notFound(id)
    }
  }

  val updateEndpoint = put("todo" / uuid :: body.as[TodoUpdate]) { (id: UUID, update: TodoUpdate) =>
    TodoDb.find(id).toFuture flatMap {
      case Some(todo) =>
        val future: Future[Todo] = TodoDb.save(todo merge update).toFuture
        future.map(Ok(_))

      case None =>
        Future.value(notFound(id))
    }
  }

  val deleteEndpoint = delete("todo" / uuid) { (id: UUID) =>
    TodoDb.delete(id).toFuture map (_ => Ok(()))
  }

  val service = (
    listEndpoint   :+:
    createEndpoint :+:
    readEndpoint   :+:
    updateEndpoint :+:
    deleteEndpoint
  ).toService

  def notFound(id: UUID) =
    NotFound(new RuntimeException(s"Not found: ${id}"))

  def main(): Unit = {
     Await.ready {
      TodoDb.init().toFuture map { unit =>
        val server = Http.server.serve(":8080", service)
        onExit { server.close() }
        Await.ready(server)
      }
    }
  }
}
