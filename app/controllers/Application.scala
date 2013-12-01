package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.test("Your new application is ready."))
  }

  def test = Action {

    // adding an item
    Ok(views.html.test("Welcome to the Six Thinking Hats!"))
  }
  
  def graph = Action {
    Ok(views.html.graph("This is gonna be the process outline"))
  }

}