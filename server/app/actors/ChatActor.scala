package actors

import org.apache.pekko.actor.{Actor, ActorRef, Props}
import ChatActor._

object ChatActor {
  case class Join(ref: ActorRef)

  case class Leave(ref: ActorRef)

  case class Broadcast(message: String,sender: ActorRef)

  def props: Props = Props(new ChatActor)
}
class ChatActor extends Actor {
  private var members: Set[ActorRef] = Set()
  override def receive : Receive = {
    case Join(ref) =>
      members += ref
      println(s"New member joined: $ref. Total members: ${members.size}")
    case Leave(ref) =>
      members -= ref
      println(s"Member left: $ref. Total members: ${members.size}")
    case Broadcast(message, sender) =>
//      println(s"Broadcasting message: $message") // Log die Nachricht
      members
        .filterNot(_ == sender) // Sender wird ausgeschlossen
        .foreach(_ ! message)
  }
}
