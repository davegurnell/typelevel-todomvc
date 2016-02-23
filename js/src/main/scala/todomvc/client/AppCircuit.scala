package todomvc.client

import diode._
import diode.react.ReactConnector
import java.util.UUID
import todomvc.core._

/**
  * AppCircuit provides the actual instance of the `AppModel` and all the action
  * handlers we need. Everything else comes from the `Circuit`
  */
object AppCircuit extends Circuit[AppModel] with ReactConnector[AppModel] {
  // define initial value for the application model
  def initialModel = AppModel(Todos(Seq()))

  override val actionHandler = combineHandlers(
    new TodoHandler(zoomRW(_.todos)((m, v) => m.copy(todos = v)).zoomRW(_.todoList)((m, v) => m.copy(todoList = v)))
  )
}

class TodoHandler[M](modelRW: ModelRW[M, Seq[Todo]]) extends ActionHandler(modelRW) {

  def updateOne(Id: UUID)(f: Todo => Todo): Seq[Todo] =
    value.map {
      case found@Todo(_, _, Id) => f(found)
      case other => other
    }

  override def handle = {
    case InitTodos =>
      println("Initializing todos")
      updated(List(Todo("Test your code!", false)))
    case AddTodo(title) =>
      updated(value :+ Todo(title, false))
    case ToggleAll(checked) =>
      updated(value.map(_.copy(completed = checked)))
    case ToggleCompleted(id) =>
      updated(updateOne(id)(old => old.copy(completed = !old.completed)))
    case Update(id, title) =>
      updated(updateOne(id)(_.copy(title = title)))
    case Delete(id) =>
      updated(value.filterNot(_.id == id))
    case ClearCompleted =>
      updated(value.filterNot(_.completed))
  }
}
