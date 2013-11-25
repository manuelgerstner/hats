package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def test = Action {

    // adding an item
    Ok(views.html.test("Welcome to the Six Thinking Hats!"))
  }

}