package todomvc.server

import cats.data.Xor
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import io.finch.{Input}
import java.util.UUID
import com.twitter.finagle.http.{Status, Method, Request, Response}
import com.twitter.util.{Await, Future}
import org.specs2.matcher.Scope
import org.specs2.mutable._
import todomvc.core._

class ApiSpec extends Specification with HttpHelpers {
  "list endpoint" should {
    "list all todos" in new ApiMocks {
      val request  = getRequest("/todo")
      val response = Await.result(api.service(request))

      response.status mustEqual Status.Ok
      response.contentAs[List[Todo]] mustEqual db.todos
    }
  }

  "create endpoint" should {
    "create a todo" in new ApiMocks {
      val request  = postRequest("/todo")(Json.obj(
        "title"     -> "Do moar stuff".asJson,
        "completed" -> false.asJson
      ))
      val response = Await.result(api.service(request))

      response.status mustEqual Status.Ok // TODO: Fails with "400 Required body not present in the request."

      val todo4 = response.contentAs[Todo]
      todo4.title     mustEqual    "Do moar stuff"
      todo4.completed mustEqual    false
      todo4.id        mustNotEqual todo1.id
      todo4.id        mustNotEqual todo2.id
      todo4.id        mustNotEqual todo3.id

      db.todos must contain(allOf(todo1, todo2, todo3, todo4))
    }
  }

  "read endpoint" should {
    "read a todo" in new ApiMocks {
      val request  = getRequest(s"/todo/${todo2.id}")
      val response = Await.result(api.service(request))

      response.status mustEqual Status.Ok
      response.contentAs[Todo] mustEqual todo2
    }

    "return a 404 if a todo was not found" in new ApiMocks {
      val request  = getRequest(s"/todo/${UUID.randomUUID}")
      val response = Await.result(api.service(request))

      response.status mustEqual Status.NotFound
    }
  }

  "update endpoint" should {
    "update a todo" in new ApiMocks {
      val request = putRequest(s"/todo/${todo2.id}")(Json.obj(
        "title"      -> "Done laundry".asJson,
        "completed" -> true.asJson
      ))
      val response = Await.result(api.service(request))

      response.status mustEqual Status.Ok // TODO: Fails with "400 Required body not present in the request."

      val actual   = response.contentAs[Todo]
      val expected = db.todos.find(_.id == todo2.id).get

      actual           mustEqual expected
      actual.title     mustEqual "Done laundry"
      actual.completed mustEqual true
      actual.id        mustEqual todo2.id
    }

    "return a 404 if a todo was not found" in new ApiMocks {
      val request  = putRequest(s"/todo/${UUID.randomUUID}")(Json.obj(
        "title"      -> "Done laundry".asJson,
        "completed" -> true.asJson
      ))
      val response = Await.result(api.service(request))

      response.status mustEqual Status.NotFound
      db.todos mustEqual List(todo1, todo2, todo3)
    }
  }

  "delete endpoint" should {
    "delete a todo" in new ApiMocks {
      val request  = deleteRequest(s"/todo/${todo2.id}")
      val response = Await.result(api.service(request))

      response.status mustEqual Status.Ok
      db.todos mustEqual List(todo1, todo3)
    }

    "return a 404 if a todo was not found" in new ApiMocks {
      val request  = deleteRequest(s"/todo/${UUID.randomUUID}")
      val response = Await.result(api.service(request))

      response.status mustEqual Status.NotFound
      db.todos mustEqual List(todo1, todo2, todo3)
    }
  }

  "sync endpoint" should {
    "replace all todos" in new ApiMocks {
      val todo4 = Todo("Eat a big lunch", false)
      val todo5 = Todo("Sleep", false)

      val request  = putRequest("/todo")(List(todo4, todo5))
      val response = Await.result(api.service(request))

      response.status mustEqual Status.Ok
      response.contentAs[List[Todo]] mustEqual List(todo4, todo5)

      db.todos mustEqual List(todo4, todo5)
    }
  }
}

trait ApiMocks extends Scope {
  val todo1 = Todo("Buy shopping"    , false)
  val todo2 = Todo("Do laundry"      , false)
  val todo3 = Todo("Take over world" , false)

  val db  = new MockDatabase(List(todo1, todo2, todo3))
  val api = new TodoApi(db)
}

trait HttpHelpers {
  def jsonRequest[A: Encoder](method: Method, uri: String)(content: A) = {
    val req = Request(method = method, uri = uri)

    val jsonContent = content.asJson.noSpaces

    req.setContentType("application/json")
    req.setContentString(jsonContent)
    req.contentLength = jsonContent.length

    req
  }

  def getRequest(uri: String) =
    Request(method = Method.Get, uri = uri)

  def postRequest[A: Encoder](uri: String)(content: A) =
    jsonRequest(Method.Post, uri)(content)

  def putRequest[A: Encoder](uri: String)(content: A) =
    jsonRequest(Method.Put, uri)(content)

  def deleteRequest(uri: String) =
    Request(Method.Delete, uri)

  implicit class ResponseOps(response: Response) {
    def contentAs[A: Decoder] =
      jackson.decode[A](response.contentString)
        .getOrElse(sys.error(s"Could not decode: ${response.contentString}"))
  }
}
