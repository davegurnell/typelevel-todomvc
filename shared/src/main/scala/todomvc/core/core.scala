package todomvc.core

import java.util.UUID

case class Todo(text: String, completed: Boolean, id: UUID = UUID.randomUUID)

object Todo {
  def create = Todo(text = "", completed = false)
}
