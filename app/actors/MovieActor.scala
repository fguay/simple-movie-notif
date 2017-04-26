package actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.event.LoggingReceive

import scala.collection.immutable.{HashSet, Queue}
import scala.concurrent.duration._

/**
 * There is one StockActor per stock symbol.  The StockActor maintains a list of users watching the stock and the stock
 * values.  Each StockActor updates a rolling dataset of randomly generated stock values.
 */
class MovieActor(movieId: String) extends Actor with ActorLogging {
  private val random = scala.util.Random

  private val fetchLatestInterval = 75.millis


  protected[this] var watchers: HashSet[ActorRef] = HashSet.empty[ActorRef]


  def receive = LoggingReceive {
    case MovieMessage(id, newMessage) =>
      watchers.foreach(_ ! MovieMessage(id, newMessage))
    case SubscibeMovie(_) =>
      // add the watcher to the list
      watchers = watchers + sender
    case UnSubscibeMovie(_) =>
      watchers = watchers - sender
      if (watchers.isEmpty) {
        context.stop(self)
      }
  }
}



case class MovieMessage(id: String, message: String)


case class SubscibeMovie(id: String)

case class UnSubscibeMovie(id: Option[String])
