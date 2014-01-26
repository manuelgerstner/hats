import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import models.Card
import models.ThinkingSession
import models.Hat
import models.User
import controllers.ThinkingSessions
import controllers.routes

@RunWith(classOf[JUnitRunner])
class UnitSpec extends Specification {

  "Models and Persistence Layer" should {
    "create and retrieve card" in new WithApplication {
      val content = "PersistenceSpecContent"
      val id = Card.create(content, ThinkingSession.dummyId, Hat.dummyId, User.dummyId, 0, 0, None, None)
      val dbCard = Card.byId(id)
      dbCard must beSome
      dbCard.get.id must equalTo(id)
      dbCard.get.content must equalTo(content)
      dbCard.get.thinkingSession.id must equalTo(ThinkingSession.dummyId)
      dbCard.get.hat.id must equalTo(Hat.dummyId)
      dbCard.get.creator.id must equalTo(User.dummyId)
      dbCard.get.imgUrl must equalTo(None)
      dbCard.get.imgMime must equalTo(None)
    }

    "create and retrive ThinkingSession" in new WithApplication {
      val topic = "PersistenceSpecContent"
      val id = ThinkingSession.create(User.dummy, topic, Hat.dummy)
      val dbSession = ThinkingSession.byId(id);
      dbSession must beSome
      dbSession.get.id must equalTo(id)
      dbSession.get.title must equalTo(topic)
      dbSession.get.owner.id must equalTo(User.dummyId)
      dbSession.get.currentHat.id must equalTo(Hat.dummyId)
    }

    "create and retrive User" in new WithApplication {
      val name = "PersistenceSpecContent"
      val id = User.create(name, None);
      val dbUser = User.byId(id);
      dbUser.get.id must equalTo(id)
      dbUser must beSome
      dbUser.get.name must equalTo(name)
      dbUser.get.mail must equalTo(None)
    }
  }

  "Sending Mails" should {
    "test send mail" in new WithApplication {
      // add some emails to the list for testing
      val emails: List[String] = Nil
      ThinkingSessions.sendInviteMails(emails, "Unit Test", routes.ThinkingSessions.index(ThinkingSession.dummyId).absoluteURL(false)(FakeRequest()))
    }
  }
}