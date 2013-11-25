package models

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

case class Idea(id: Long, title: String, content: String)

object Idea {

  val simple = {
    get[Long]("id") ~
      get[String]("title") ~
      get[String]("content") map{
      case id~title~content => Idea(id, title, content);
    }
  }

  def all(): List[Idea] = {
    DB.withConnection { implicit connection =>
      SQL("select * from idea").as(Idea.simple *)
    }
  }

  def create(title: String, content: String) {
    DB.withConnection { implicit connection =>
      SQL("insert into idea (title, content) values ({title},{content})").on(
        'title -> title,
        'content -> content
      ).executeUpdate()
    }
  }

  def delete(id: Long) {
    DB.withConnection{ implicit connection =>
      SQL("delete from idea where id = {id}").on(
        'id -> id
      ).executeUpdate()
    }
  }

}
