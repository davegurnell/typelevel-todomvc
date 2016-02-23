package todomvc.client

import java.util.UUID
import todomvc.core._

// Define our application model
case class AppModel(todos: Todos)

case class Todos(todoList: Seq[Todo])

sealed abstract class TodoFilter(val link: String, val title: String, val accepts: Todo => Boolean)

object TodoFilter {
  object All extends TodoFilter("", "All", _ => true)
  object Active extends TodoFilter("active", "Active", !_.completed)
  object Completed extends TodoFilter("completed", "Completed", _.completed)

  val values = List[TodoFilter](All, Active, Completed)
}

// define actions
sealed trait TodoAction
final case object InitTodos extends TodoAction
final case class AddTodo(title: String) extends TodoAction
final case class ToggleAll(checked: Boolean) extends TodoAction
final case class ToggleCompleted(id: UUID) extends TodoAction
final case class Update(id: UUID, title: String) extends TodoAction
final case class Delete(id: UUID) extends TodoAction
final case class SelectFilter(filter: TodoFilter) extends TodoAction
final case object ClearCompleted extends TodoAction
