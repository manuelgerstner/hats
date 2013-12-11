package models.forms
import anorm._
import anorm.SqlParser._
import play.api.db._
case class FormConfig(whHatt: String, whAlonet: String, yeHatt: String, yeAlonet: String, reHatt: String, reAlonet: String, grHatt: String, grAlonet: String, blHatt: String, blAlonet: String, bluHatt: String, bluAlonet: String  )