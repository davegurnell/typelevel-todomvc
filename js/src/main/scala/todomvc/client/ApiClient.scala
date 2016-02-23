package todomvc.client

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import java.util.UUID
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.ext.Ajax
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import todomvc.core._

object ApiClient {
  val apiRoot = "http://localhost:8080"


  def loadTodos: Future[List[Todo]] =
    Ajax.get(s"${apiRoot}/todo").map(decodeTodoList)

  def syncTodos(todos: Seq[Todo]): Future[List[Todo]] =
    Ajax.put(s"${apiRoot}/todo", todos.asJson.noSpaces).map(decodeTodoList)

  def decodeTodoList(xhr: XMLHttpRequest): List[Todo] =
    decode[List[Todo]](xhr.responseText)
      .getOrElse(sys.error("Could not decode JSON " + xhr.responseText))
}
