package controllers
import play.api.mvc.Controller

object ThinkingSession extends Controller {

  /**
   * TODO: Show the the dashboard for the current session
   */
  def index(id: Long) = TODO

  /**
   * TODO: Update Session state to respective hat, show session index of new hat.
   * Push info to all clients
   */
  def goToHat(id: Long, hat: String) = TODO

  /**
   * TODO: Conclude session and redirect to review page
   */
  def finish(id: Long) = TODO

}