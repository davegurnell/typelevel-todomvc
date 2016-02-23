package todomvc.server

import com.twitter.util.Future
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._
import java.util.UUID
import todomvc.core._

class TodoApi(db: TodoDatabase) {
  import AsyncImplicits._

  lazy val listEndpoint = get("todo") {
    db.list().toFuture map (Ok(_))
  }

  val createEndpoint = post("todo" :: body.as[UUID => Todo]) { (create: UUID => Todo) =>
    db.save(create(UUID.randomUUID)).toFuture map (Ok(_))
  }

  val readEndpoint = get("todo" / uuid) { (id: UUID) =>
    db.find(id).toFuture map {
      case Some(todo) => Ok(todo)
      case None       => notFound(id)
    }
  }

  val updateEndpoint = put("todo" / uuid :: body.as[UUID => Todo]) { (id: UUID, update: UUID => Todo) =>
    db.find(id).toFuture flatMap {
      case Some(todo) => db.save(update(todo.id)).toFuture.map(Ok(_))
      case None       => Future.value(notFound(id))
    }
  }

  val deleteEndpoint = delete("todo" / uuid) { (id: UUID) =>
    db.delete(id).toFuture map { deleted =>
      if(deleted) Ok(()) else notFound(id)
    }
  }

  val syncEndpoint = put("todo" :: body.as[List[Todo]]) { (todos: List[Todo]) =>
    db.sync(todos).toFuture map (Ok(_))
  }

  val endpoints =
    listEndpoint   :+:
    createEndpoint :+:
    readEndpoint   :+:
    updateEndpoint :+:
    deleteEndpoint :+:
    syncEndpoint

  val service = endpoints.toService

  private[todomvc] def notFound(id: UUID) =
    NotFound(new RuntimeException(s"Todo item not found: ${id}"))
}
