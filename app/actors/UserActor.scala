package actors

import javax.inject._

import akka.actor._
import akka.event.LoggingReceive
import com.google.inject.assistedinject.Assisted
import play.api.Configuration
import play.api.libs.concurrent.InjectedActorSupport
import play.api.libs.json._

class UserActor @Inject()(@Assisted out: ActorRef,
                          @Named("moviesActor") moviesActor: ActorRef,
                          configuration: Configuration) extends Actor with ActorLogging {


  override def preStart(): Unit = {
    super.preStart()

    configureDefaultMovie()
  }

  def configureDefaultMovie(): Unit = {
    import collection.JavaConversions._
    val defaultMovies = configuration.getStringList("default.movies").get
    log.info(s"Creating user actor with default movie $defaultMovies")

    for (movieId <- defaultMovies) {
      moviesActor ! SubscibeMovie(movieId)
    }
  }

  override def receive: Receive = LoggingReceive {
    case MovieMessage(id, message) =>
      log.info(s"Yeh ! New movie  $id message $message")
      val movieUpdateMessage = Json.obj("type" -> "movieupdate", "id" -> id, "message" -> message)
      out ! movieUpdateMessage

    case json: JsValue =>
      // When the user types in a stock in the upper right corner, this is triggered
      val id = (json \ "id").as[String]
      log.info(s"Subscribe to movie :  $id")
      moviesActor ! SubscibeMovie(id)
  }
}

class UserParentActor @Inject()(childFactory: UserActor.Factory) extends Actor with InjectedActorSupport with ActorLogging {
  import UserParentActor._

  override def receive: Receive = LoggingReceive {
    case Create(id, out) =>
      val child: ActorRef = injectedChild(childFactory(out), s"userActor-$id")
      sender() ! child
  }
}

object UserParentActor {
  case class Create(id: String, out: ActorRef)
}

object UserActor {
  trait Factory {
    // Corresponds to the @Assisted parameters defined in the constructor
    def apply(out: ActorRef): Actor
  }
}
