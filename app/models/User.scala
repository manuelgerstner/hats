package models

import play.api.db.DB
import play.api.db._

import play.api.Play.current
import anorm._
import anorm.SqlParser._

/**
 * Models participants and moderators/creators of ThinkingSession. No authentication done by us, so
 * we keep the data to a minimum.
 * Possible Additions: eMail, Image/ImageUrl, last (browser) session, list of ThinkingSessions (access)
 *
 * We do not need the Users for the this Monday (2nd Dec)
 *
 * @author Nemo
 */
case class User(id: Long, name: String)

object User {

  /**
   * ORM simple
   */
  def simple = {
    get[Long]("id") ~
      get[String]("name") map {
        case id ~ name => User(id, name)
      }
  }

  /**
   * Retrieve all users in the system
   */
  def all(): List[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user").as(User.simple *)
    }
  }

  /**
   * Find a user by id
   */
  def getById(id: Long): User = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where id = {id}").on(
        'id -> id
      ).as(User.simple *) head
    }
  }

  /**
   * Create a new user by a name.
   * This will NOT return the created User!
   */
  def create(name: String) = {
    DB.withConnection { implicit connection =>
      SQL("insert into user (name) values ({name})").on(
        'name -> name
      ).executeUpdate()
    }
  }

  /**
   * Delete a user
   */
  def delete(user: User) = {
    DB.withConnection { implicit connection =>
      SQL("delete from user where id = {id}").on(
        'id -> user.id
      ).executeUpdate()
    }
  }

  /**
   * some dummy users for dev purposes
   */
  def getDummyUser1(): User = {
    all() head
  }

  def getDummyUser2(): User = {
    (all() tail) head
  }

  def getDummyUser3(): User = {
    ((all() tail) tail) head
  }

  def getDummyUser4(): User = {
    (((all() tail) tail) tail) head
  }
}