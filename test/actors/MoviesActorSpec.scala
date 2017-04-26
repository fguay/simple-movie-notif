package actors

import akka.actor.Props
import akka.testkit.TestProbe

import scala.concurrent.duration._

class MoviesActorSpec extends TestKitSpec {

  "MoviesMessage" should {
    val id = "123"

    "send a Movie message to the user" in {
      val probe = new TestProbe(system)
      val moviesActor = system.actorOf(Props[MoviesActor])

      // create an actor which will test the UserActor
      val userActor = system.actorOf(Props(new ProbeWrapper(probe)))

      // Fire off the message, setting the sender as the UserActor
      // Simulates sending the message as if it was sent from the userActor
      moviesActor.tell(SubscibeMovie(id), userActor)

      // Should create a new movieActor as a child
      val sub = probe.receiveOne(500 millis)

      moviesActor!MovieMessage(id, "test")

      val message = probe.receiveOne(500 millis)


    }

  }
}
