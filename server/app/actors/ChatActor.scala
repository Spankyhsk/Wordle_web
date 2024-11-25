package actors

import org.apache.pekko.actor.{Actor, ActorRef, Props}
import actors.ChatMessages._

object ChatActor {
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
    case BroadcastMessage(message) =>
//      println(s"Broadcasting message: $message") // Log die Nachricht
      members.foreach(_ ! BroadcastMessage(message)) // Nachricht weiterleiten
      
  }
}
