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
  def getByName(name: String): Hat = {
    all() filter ((hat: Hat) => hat.name == name) head
  }

  /**
   * Retrieves a hat by its id
   */
  def getById(id: Long): Hat = {
    all() filter ((hat: Hat) => hat.id == id) head
  }

  /**
   * Retrieves the human-readable name for a hat
   */
  def getHatName(id: Long): String = {
    (getById(id).name) toLowerCase
  }

  /**
   * for development purposes this will return the white hat by default
   */
  def dummyHat(): Hat = {
    all() head
  }

  def dummyHatId: Long = 1
}
