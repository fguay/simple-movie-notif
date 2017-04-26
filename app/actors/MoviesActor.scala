package actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingReceive

class MoviesActor extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case movieMessage@MovieMessage(id, message) =>
      // get or create the StockActor for the id and forward this message
      context.child(id).getOrElse {
        context.actorOf(Props(new MovieActor(id)), id)
      } forward movieMessage
    case watchMovie@SubscibeMovie(id) =>
      // get or create the StockActor for the id and forward this message
      context.child(id).getOrElse {
        context.actorOf(Props(new MovieActor(id)), id)
      } forward watchMovie
    case unwatchMovie@UnSubscibeMovie(Some(id)) =>
      // if there is a StockActor for the symbol forward this message
      context.child(id).foreach(_.forward(unwatchMovie))
    case unwatchMovie@UnSubscibeMovie(None) =>
      // if no symbol is specified, forward to everyone
      context.children.foreach(_.forward(unwatchMovie))
  }

}
