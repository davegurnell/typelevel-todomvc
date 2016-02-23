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
}

class DoobieTodoDatabase extends TodoDatabase {
  implicit val uuidMeta = Meta[String].nxmap[UUID](UUID.fromString, _.toString)

  val transactor = for {
    xa <- H2Transactor[Task]("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "")
    _  <- xa.setMaxConnections(10)
  } yield xa

  def init(): Task[Unit] = transactor.flatMap { xa =>
    val query = for {
      _ <- sql"""create table todos(text varchar, completed boolean, id varchar primary key);""".update.run
      _ <- sql"""insert into todos values ('Create demo webapp', false, ${UUID.randomUUID});""".update.run
      _ <- sql"""insert into todos values ('Learn Typelevel libraries', false, ${UUID.randomUUID});""".update.run
      _ <- sql"""insert into todos values ('Take over world', false, ${UUID.randomUUID});""".update.run
    } yield ()

    query.transact(xa)
  }

  def list(): Task[List[Todo]] = transactor.flatMap { xa =>
    sql"""
      select * from todos
    """
      .query[Todo]
      .list
      .transact(xa)
  }

  def find(id: UUID): Task[Option[Todo]] = transactor.flatMap { xa =>
    sql"""
      select * from todos where id = $id
    """
      .query[Todo]
      .option
      .transact(xa)
  }

  def save(todo: Todo): Task[Todo] = transactor.flatMap { xa =>
    sql"""
      merge into todos (text, completed, id) key (id)
      values (${todo.text}, ${todo.completed}, ${todo.id});
    """
      .update.run
      .transact(xa)
      .map(_ => todo)
  }

  def delete(id: UUID): Task[Boolean] = transactor.flatMap { xa =>
    sql"""
      delete from todos where id = $id
    """
      .update.run
      .transact(xa)
      .map(_ > 0)
  }
}