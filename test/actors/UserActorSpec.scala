package actors

import akka.actor._
import akka.testkit.{TestActorRef, _}
import com.typesafe.config.ConfigFactory
import org.scalatest.MustMatchers
import play.api.libs.json._

import scala.concurrent.duration._

class UserActorSpec extends TestKitSpec with MustMatchers {

  "UserActor" should {

    val id = "123"
    val message = "ABC"
    val history = scala.collection.immutable.Seq[Double](0.1, 1.0)
    val configuration = play.api.Configuration.apply(ConfigFactory.parseString(
      """
        |default.movies = ["123", "234", "345"]
      """.stripMargin))

    "send a movie when receiving a message" in {
      val out = TestProbe()
      val stocksActor = TestProbe()

      val userActorRef = TestActorRef[UserActor](Props(new UserActor(out.ref, stocksActor.ref, configuration)))
      val userActor = userActorRef.underlyingActor

      // send off the stock update...
      userActor.receive(MovieMessage(id, message))

      // ...and expect it to be a JSON node.
      val jsObj: JsObject = out.receiveOne(500 millis).asInstanceOf[JsObject]
      jsObj \ "type" mustBe JsDefined(JsString("movieupdate"))
      jsObj \ "id" mustBe JsDefined(JsString(id))
      jsObj \ "message" mustBe JsDefined(JsString(message))
    }

  }

}
