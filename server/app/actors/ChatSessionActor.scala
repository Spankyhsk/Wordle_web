package actors

import org.apache.pekko.actor.{Actor, ActorRef, Props}
import actors.ChatActor.*
import actors.ChatSessionActor.ClientMessage

object ChatSessionActor {
  case class ClientMessage(msg: String)

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
    case ClientMessage(msg) =>
      println(s"Received message from client: $msg")
      chatActor ! Broadcast(msg, self) // Eingehende Nachricht weiterleiten
    case msg: String =>
      out ! msg // Nachricht an WebSocket-Client senden
  }
}
