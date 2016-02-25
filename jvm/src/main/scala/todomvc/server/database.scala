package todomvc.server

import doobie.contrib.h2.h2transactor._
import doobie.imports._
import java.util.UUID
import scalaz._
import scalaz.Scalaz._
import scalaz.concurrent.Task
import todomvc.core._

trait TodoDatabase {
  val init: Task[Unit]
  val list: Task[List[Todo]]
  def find(id: UUID): Task[Option[Todo]]
  def save(todo: Todo): Task[Todo]
  def delete(id: UUID): Task[Boolean]
  def sync(todos: List[Todo]): Task[List[Todo]]
}

object DoobieTodoDatabase {

  val create: Task[DoobieTodoDatabase] = 
    for {
      xa <- H2Transactor[Task]("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "")
      _  <- xa.setMaxConnections(10)
      db  = new DoobieTodoDatabase(xa)
      _  <- db.init
    } yield db

}

class DoobieTodoDatabase(xa: Transactor[Task]) extends TodoDatabase {

  implicit val uuidMeta: Meta[UUID] = 
    Meta[String].nxmap(UUID.fromString, _.toString)

  val init: Task[Unit] = {
    val action = for {
      _ <- createTableQuery
      _ <- saveQuery(Todo("Create demo webapp", false))
      _ <- saveQuery(Todo("Learn Typelevel libraries", false))
      _ <- saveQuery(Todo("Take over world", false))
    } yield ()
    action.transact(xa)
  }

  val list: Task[List[Todo]] =
    listQuery.transact(xa)

  def find(id: UUID): Task[Option[Todo]] =
    findQuery(id).transact(xa)

  def save(todo: Todo): Task[Todo] =
    saveQuery(todo).as(todo).transact(xa)

  def delete(id: UUID): Task[Boolean] =
    deleteQuery(id).transact(xa)

  def sync(todos: List[Todo]): Task[List[Todo]] = {
    val action = for {
      _     <- deleteAllQuery
      todos <- todos.traverse(saveQuery)
    } yield todos
    action.transact(xa)
  }

  lazy val createTableQuery: ConnectionIO[Int] =
    sql"""
    create table todos(title varchar not null, completed boolean not null, id varchar primary key);
    """.update.run

  lazy val listQuery: ConnectionIO[List[Todo]] =
    sql"""
    select title, completed, id from todos
    """.query[Todo].list

  def findQuery(id: UUID): ConnectionIO[Option[Todo]] =
    sql"""
    select title, completed, id from todos where id = $id
    """.query[Todo].option

  def saveQuery(todo: Todo): ConnectionIO[Todo] =
    sql"""
    merge into todos (title, completed, id) key (id)
    values (${todo.title}, ${todo.completed}, ${todo.id});
    """.update.run.map(_ => todo)

  def deleteQuery(id: UUID): ConnectionIO[Boolean] =
    sql"""
    delete from todos where id = $id
    """.update.run.map(_ > 0)

  val deleteAllQuery: ConnectionIO[Boolean] =
    sql"""
    delete from todos
    """.update.run.map(_ > 0)

}
