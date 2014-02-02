package controllers

import play.api._
import play.api.mvc.Controller
import play.api.mvc._
import play.api.mvc.Results
import controllers._
import views.html.defaultpages.notFound
import models._

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
    val sess1: Long = id * 10
    val sess2: Long = (id - 1) * 10
    val sess3: Long = (id - 2) * 10
    Ok(views.html.dashboard(sess1, sess2, sess3, HatElapsed.byHatNoCards(id), HatElapsed.hatTimeForCurrentSession(id), HatElapsed.byHatNoCardsPrev(id), HatElapsed.byHatNoCardsPrev2(id)))

  }
  /**
   * save the Elapsed time per hat for Current Session
   */
  def saveDuration(id: Long, hat_name: String, elapsedTime: Long) = Action {
    Logger.debug("Save ElapsedTime:" + elapsedTime + hat_name + id)
    HatElapsed.insertElapsedTime(id, hat_name, elapsedTime)
    Ok("okay")
  }
}