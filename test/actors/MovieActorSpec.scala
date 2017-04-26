package actors

import akka.actor._
import akka.testkit._

import scala.concurrent.duration._
import scala.collection.immutable.HashSet

class MovieActorSpec extends TestKitSpec {


  "SubscribeMovie" should {
    val id = "123"
    val message = "123"

    "notify watchers when a new movie message is received" in {
      // Create a Movie actor with a stubbed out Moviequote price and watcher
      val probe = new TestProbe(system)
      val price = 1234.0
      val MoviesActor = system.actorOf(Props[MoviesActor])

      system.actorOf(Props(new ProbeWrapper(probe)))

      // Fire off the message...
      MoviesActor ! MovieMessage(id, message)

      // ... and ask the probe if it got the MovieUpdate message.
      val actualMessage = probe.receiveOne(500 millis)

    }

    "add a watcher and send a MovieHistory message to the user" in {
      val probe = new TestProbe(system)

      // Create a standard MovieActor.
      val MovieActor = system.actorOf(Props(new MovieActor(id)))

      // create an actor which will test the UserActor
      val userActor = system.actorOf(Props(new ProbeWrapper(probe)))

      // Fire off the message, setting the sender as the UserActor
      // Simulates sending the message as if it was sent from the userActor
      MovieActor.tell(SubscibeMovie(id), userActor)

      // the userActor will be added as a watcher and get a message with the Movie history
      val userActorMessage = probe.receiveOne(500.millis)
    }
  }

}
