package todomvc.client

import diode._
import diode.react.ReactConnector
import java.util.UUID
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import todomvc.core._

/**
  * AppCircuit provides the actual instance of the `AppModel` and all the action
  * handlers we need. Everything else comes from the `Circuit`
  */
object AppCircuit extends Circuit[AppModel] with ReactConnector[AppModel] {
  // define initial value for the application model
  def initialModel = AppModel(Seq.empty)

  override val actionHandler = combineHandlers(
    new TodoHandler(zoomRW(_.todos)((m, v) => m.copy(todos = v)))
  )
}

class TodoHandler[M](modelRW: ModelRW[M, Seq[Todo]]) extends ActionHandler(modelRW) {

  def updateOne(Id: UUID)(f: Todo => Todo): Seq[Todo] =
    value.map {
      case found@Todo(_, _, Id) => f(found)
      case other => other
    }

  def syncUpdated(todos: Seq[Todo]) =
    updated(todos, Effect(ApiClient.syncTodos(todos)))

  override def handle = {
    case InitTodos(todos) =>
      println("Initializing todos")
      syncUpdated(todos)
    case AddTodo(title) =>
      syncUpdated(value :+ Todo(title, false))
    case ToggleAll(checked) =>
      syncUpdated(value.map(_.copy(completed = checked)))
    case ToggleCompleted(id) =>
      syncUpdated(updateOne(id)(old => old.copy(completed = !old.completed)))
    case Update(id, title) =>
      syncUpdated(updateOne(id)(_.copy(title = title)))
    case Delete(id) =>
      syncUpdated(value.filterNot(_.id == id))
    case ClearCompleted =>
      syncUpdated(value.filterNot(_.completed))
  }
}
