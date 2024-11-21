package actors

import akka.actor.{Actor, ActorRef, Props}
import ChatActor._

object ChatActor {
  case class Join(ref: ActorRef)

  case class Leave(ref: ActorRef)

  case class Broadcast(message: String)

  def props: Props = Props(new ChatActor)
}
class ChatActor extends Actor {
  private var members: Set[ActorRef] = Set()
  override def receive : Receive = {
    case Join(ref) =>
      members += ref
    case Leave(ref) =>
      members -= ref
    case Broadcast(message) =>
      members.foreach(_ ! message)
  }
}
