package todomvc.core

import java.util.UUID

case class Todo(title: String, completed: Boolean, id: UUID = UUID.randomUUID)

object Todo {
  def create = Todo(title = "", completed = false)
}
