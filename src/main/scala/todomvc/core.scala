package todomvc

import java.util.UUID

case class Todo(text: String, completed: Boolean, id: UUID = UUID.randomUUID)

case class TodoUpdate(text: Option[String], completed: Option[Boolean])

object Todo {
  def create = Todo(text = "", completed = false)

  var list: List[Todo] = List(
    Todo(text = "Buy milk"            , completed = false),
    Todo(text = "Invent time travel"  , completed = false),
    Todo(text = "Take over the world" , completed = false)
  )
}