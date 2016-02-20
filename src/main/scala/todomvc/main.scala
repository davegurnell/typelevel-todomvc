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
import shapeless._

object Api extends TwitterServer {
  lazy val listEndpoint   = get("todo") {
    Ok(Todo.list)
  }

  val createEndpoint = post("todo" :: body.as[TodoUpdate]) { (update: TodoUpdate) =>
    val todo = Todo.create merge update
    Todo.list = Todo.list :+ todo
    Ok(todo)
  }

  val readEndpoint = get("todo" / uuid) { (id: UUID) =>
    Todo.list.find(_.id == id) match {
      case Some(todo) => Ok(todo)
      case None       => notFound(id)
    }
  }

  val updateEndpoint = put("todo" / uuid :: body.as[TodoUpdate]) { (id: UUID, update: TodoUpdate) =>
    Todo.list.find(_.id == id) match {
      case Some(todo) =>
        Todo.list = Todo.list map (t => if(t.id == id) todo merge update else t)
        Ok(todo)

      case None => notFound(id)
    }
  }

  val deleteEndpoint = delete("todo" / uuid) { (id: UUID) =>
    Todo.list.find(_.id == id) match {
      case Some(todo) =>
        Todo.list = Todo.list.filterNot(_.id == id)
        Ok(())

      case None => notFound(id)
    }
  }

  val service =
    (listEndpoint :+: createEndpoint :+: readEndpoint :+: updateEndpoint :+: deleteEndpoint).toService

  def notFound(id: UUID) =
    NotFound(new RuntimeException(s"Not found: ${id}"))

  def main(): Unit = {
    val server = Http.server.serve(":8080", service)
    onExit { server.close() }
    Await.ready(server)
  }
}
