package controllers

import play.api._
import play.api.mvc.Controller
import play.api.mvc._
import play.api.mvc.Results
import controllers._
import views.html.defaultpages.notFound
import models.ThinkingSession

/**
 * Dashboard Controller responsible for showing the summary report
 *
 * @author: Anamika
 */
object Dashboard extends Controller {

  /**
   * show summary report
   */
  def showReport(id: Long) = Action { implicit request =>
  //Action { implicit request =>
  //Logger.debug("Dashboard")
  	Ok(views.html.dashboard())

  }
}