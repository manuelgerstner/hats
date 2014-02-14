package controllers

import play.api._
import play.api.mvc.Controller
import play.api.mvc._
import play.api.mvc.Results
import controllers._
import views.html.defaultpages.notFound
import models._
import java.util.Date
import org.joda.time._
import scala.collection.mutable.Map

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
    // val hatTime = new Map[String, Long]()
    val hatNameTime = Map[String, Long]() //mutable
    //hatTime: Map[String, Long] = Map()
    var hatName: String = "White"
    var creTime: DateTime = new DateTime
    var creatTime: DateTime = new DateTime
    var eTime: DateTime = new DateTime
    val test = request.cookies.get(User.idCookie)
    request.cookies.get(User.idCookie) match {
      case Some(cookie) => // found user cookie
        User.byCookie(cookie) match {
          case Some(user) => // user in db
            ThinkingSession.byId(id) match {
              case Some(session) => // session exists
                if (ThinkingSession.checkUser(session, user)) // check if user is part of session
                {
                  val eventList: List[Event] = Event.byThinkingSession(id) // all event List for Current Session
                  val hats: List[Hat] = Hat.all()
                  for (sHat <- hats) {
                    for (sEvent <- eventList) {
                      if ((sHat.id == sEvent.hat.id) && (sEvent.eventType == "createSession")) {
                        var crTime: Date = sEvent.time;
                        creatTime = new DateTime(crTime)
                        creTime = creatTime
                        //Logger.debug("CreateWhite::" + creTime)
                      } else if ((sHat.id == sEvent.hat.id) && (sEvent.eventType == "moveHat")) {
                        var crTime: Date = sEvent.time;
                        eTime = new DateTime(crTime)
                        //Logger.debug("ETIme::" + eTime)
                        val elapsedTime = (eTime.getMillis() - creTime.getMillis()) / 1000
                        hatNameTime += (hatName -> elapsedTime)
                        creTime = eTime
                      }
                    }
                    hatName = sHat.name
                    //Logger.debug("Hatname::" + hatName)
                  }
                  var endTime = DateTime.now()
                  val elapsedTime1 = (endTime.getMillis() - creTime.getMillis()) / 1000
                  hatNameTime += ((hatName -> elapsedTime1))
                  val hatElapsedTime: List[(String, Long)] = hatNameTime.toList
                  //Logger.debug("Elapsed TIme::" + hatElapsedTime)
                  //Ok(views.html.dashboard(hatElapsedTime, Card.byOnlyInSession(id), byUserCardList(id), Event.byThinkingSession(id)))
                  Ok(views.html.dashboard(hatElapsedTime, Card.byOnlyInSession(id), byUserCardList(id), Event.byThinkingSession(id), countHatsforUser(id)))
                } else
                  BadRequest
              case None =>
                NotFound
            }
          case None => BadRequest
        }
      case None => BadRequest;
    }

  }
  def byUserCardList(id: Long): List[(String, List[Long])] = {
    var usrIDs: List[Long] = Card.byOnlyInSession(id) //only the Users
    val resultVal = Map[String, List[Long]]() //mutable 
    val hCards = Map[String, Long]() //mutable 
    val hats: List[Hat] = Hat.all();
    for (sHat <- hats) {
      val hList: List[Long] = checkHatsforUser(id, sHat)
      resultVal += (sHat.name -> hList)
    }
    Logger.debug("User for Current Session::" + resultVal)
    resultVal.toList
  }

  def checkHatsforUser(id: Long, sHat: Hat): List[Long] = {
    var usrIDs: List[Long] = Card.byOnlyInSession(id) //only the Users
    var mUList = scala.collection.mutable.ListBuffer[Long]()
    for (sUsr <- usrIDs) {
      mUList += Card.byCardsforUser(id, sHat.id, sUsr)
    }
    mUList.toList
  }
  def countHatsforUser(id: Long): List[(String, List[(String, Long)])] = {
    var usrIDs: List[Long] = Card.byOnlyInSession(id) //only the Users
    var uName: String = new String()
    //var mUList = scala.collection.mutable.ListBuffer[Long]()
    val uCards = Map[String, Long]() //mutable 
    val resultVal = Map[String, List[(String, Long)]]() //mutable 
    val hats: List[Hat] = Hat.all();
    for (sHat <- hats) {
      for (sUsr <- usrIDs) {
        uName = User.getUserName(sUsr)
        val cardNo: Long = Card.byCardsforUser(id, sHat.id, sUsr)
        uCards += (uName -> cardNo)
      }
      resultVal += (sHat.name -> uCards.toList)
    }
    Logger.debug("UserId With Cards: " + resultVal.toList)
    resultVal.toList
  }
}