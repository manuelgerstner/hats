package models.forms
import anorm._
import anorm.SqlParser._
import play.api.db._

/**
 * Stripped down Form Model to protect the "secret" fields like db unique key etc.
 * Will be expanded as soon as we have image urls etc...
 * @author Nemo
 */
case class FormCard(content: String, hat: String)