import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import models.Card
import models.ThinkingSession
import models.Hat
import models.User

@RunWith(classOf[JUnitRunner])
class PersistenceSpec extends Specification {

  "Persistence Layer" should {
    "create new card in db without img" in new WithApplication {
      val content = "PersistenceSpecContent"
      val id = Card.create(content, ThinkingSession.dummyId, Hat.dummyId, User.dummyId, None, None)
      val dbCard = Card.byId(id)
      dbCard must beSome
      dbCard.get.content must equalTo(content)
      dbCard.get.thinkingSession.id must equalTo(ThinkingSession.dummyId)
      dbCard.get.hat.id must equalTo(Hat.dummyId)
      dbCard.get.creator.id must equalTo(User.dummyId)
      dbCard.get.imgUrl must equalTo(None)
      dbCard.get.imgMime must equalTo(None)

    }
  }
}