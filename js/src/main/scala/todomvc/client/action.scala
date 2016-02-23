package todomvc.client

sealed trait AppAction

final case class CreateTodo(text: String) extends AppAction
