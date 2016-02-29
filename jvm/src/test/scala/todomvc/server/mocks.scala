package todomvc.server

import com.twitter.io.Buf
import java.util.UUID
import org.specs2.mutable._
import scalaz.concurrent.Task
import todomvc.core._

class MockDatabase(var todos: List[Todo]) extends TodoDatabase {
  val init: Task[Unit] =
    Task.now(())

  val list: Task[List[Todo]] =
    Task.now(todos)

  def find(id: UUID): Task[Option[Todo]] =
    Task.now(todos.find(_.id == id))

  def save(todo: Todo): Task[Todo] = {
    if(todos.exists(_.id == todo.id)) {
      todos = todos.map(t => if(t.id == todo.id) todo else t)
    } else {
      todos = todos :+ todo
    }

    Task.now(todo)
  }

  def delete(id: UUID): Task[Boolean] = {
    val exists = todos.exists(_.id == id)
    todos = todos.filterNot(_.id == id)
    Task.now(exists)
  }

  def sync(todos: List[Todo]): Task[List[Todo]] = {
    this.todos = todos
    Task.now(todos)
  }
}
