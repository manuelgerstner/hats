package models

import play.api.db.DB
import play.api.db._

import play.api.Play.current
import anorm._
import anorm.SqlParser._

/**
 * A "dumb" model class that only holds static preloaded values (the 6 hats) with names (possibly some more info later on)
 * @author Nemo
 */
case class Hat(id: Long, name: String)

object Hat {

  /**
   * ORM simple
   */
  val DBParser = {
    get[Long]("id") ~
      get[String]("name") map {
        case id ~ name => Hat(id, name);
      }
  }

  /**
   * Retrieve all hats
   */
  def all(): List[Hat] = {
    DB.withConnection { implicit connection =>
      SQL("select * from hat").as(Hat.DBParser *)
    }
  }

  /**
   * Retrieves a hat by its name
   */
  def byName(name: String): Hat = {
    all() filter ((hat: Hat) => hat.name.toLowerCase == name.toLowerCase) head
  }

  /**
   * Retrieves a hat by its id
   */
  def byId(id: Long): Hat = {
    all() filter ((hat: Hat) => hat.id == id) head
  }

  /**
   * Retrieves the human-readable name for a hat
   */
  def nameById(id: Long): String = {
    (byId(id).name) toLowerCase
  }

  /**
   * for development purposes this will return the white hat by default
   */
  val dummy: Hat = all.head
  def dummyId: Long = 1
}
