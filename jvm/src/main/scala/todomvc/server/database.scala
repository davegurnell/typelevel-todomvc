package todomvc.server

import doobie.contrib.h2.h2transactor._
import doobie.imports._
import java.util.UUID
import scalaz._
import scalaz.Scalaz._
import scalaz.concurrent.Task
import todomvc.core._

trait TodoDatabase {
  def init(): Task[Unit]
  def list(): Task[List[Todo]]
  def find(id: UUID): Task[Option[Todo]]
  def save(todo: Todo): Task[Todo]
  def delete(id: UUID): Task[Boolean]
  def sync(todos: List[Todo]): Task[List[Todo]]
}

class DoobieTodoDatabase extends TodoDatabase {
  implicit val uuidMeta = Meta[String].nxmap[UUID](UUID.fromString, _.toString)

  val transactor = for {
    xa <- H2Transactor[Task]("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "")
    _  <- xa.setMaxConnections(10)
  } yield xa

  def init(): Task[Unit] =
    transactor.flatMap { xa =>
      val action = for {
        _ <- createTableQuery()
        _ <- saveQuery(Todo("Create demo webapp", false))
        _ <- saveQuery(Todo("Learn Typelevel libraries", false))
        _ <- saveQuery(Todo("Take over world", false))
      } yield ()

      action.transact(xa)
    }

  def list(): Task[List[Todo]] =
    transactor.flatMap { xa => listQuery().transact(xa) }

  def find(id: UUID): Task[Option[Todo]] =
    transactor.flatMap { xa =>
      findQuery(id).transact(xa)
    }

  def save(todo: Todo): Task[Todo] =
    transactor.flatMap { xa =>
      saveQuery(todo).transact(xa).map(_ => todo)
    }

  def delete(id: UUID): Task[Boolean] =
    transactor.flatMap { xa =>
      deleteQuery(id).transact(xa)
    }

  def sync(todos: List[Todo]): Task[List[Todo]] =
    transactor.flatMap { xa =>
      val action = for {
        _     <- deleteAllQuery()
        todos <- todos.map(saveQuery).sequenceU
      } yield todos

      action.transact(xa)
    }

  def createTableQuery(): ConnectionIO[Int] =
    sql"""
    create table todos(title varchar, completed boolean, id varchar primary key);
    """.update.run

  def listQuery(): ConnectionIO[List[Todo]] =
    sql"""
    select * from todos
    """.query[Todo].list

  def findQuery(id: UUID): ConnectionIO[Option[Todo]] =
    sql"""
    select * from todos where id = $id
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

  def deleteAllQuery(): ConnectionIO[Boolean] =
    sql"""
    delete from todos
    """.update.run.map(_ > 0)
}
