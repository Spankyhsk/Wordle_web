package actors

import org.apache.pekko.actor.{Actor, ActorRef, Props}
import actors.ChatActor._

object ChatSessionActor {
  def props(out: ActorRef, chatActor: ActorRef): Props =
    Props(new ChatSessionActor(out, chatActor))
}

class ChatSessionActor(out: ActorRef, chatActor: ActorRef) extends Actor {

  override def preStart(): Unit = {
    chatActor ! Join(self)
  }

  override def postStop(): Unit = {
    chatActor ! Leave(self)
  }
  
  def receive: Receive = {
    case msg: String =>
      chatActor ! Broadcast(msg) // Eingehende Nachricht weiterleiten
    case outgoingMessage: String =>
      out ! outgoingMessage // Nachricht an WebSocket-Client senden
  }
}
