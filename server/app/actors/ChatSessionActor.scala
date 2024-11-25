package actors

import org.apache.pekko.actor.{Actor, ActorRef, Props}
import actors.ChatMessages._
import actors.ChatSessionActor._

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
    case msg: String =>
      println(s"Received raw message: $msg")
      self ! ClientMessage(msg)
    case ClientMessage(msg: String) =>
      println(s"Received message from client: $msg")
      chatActor ! BroadcastMessage(msg) // Nachricht weiterleiten
    case BroadcastMessage(content) =>
      println(s"Received broadcast: $content")
      out ! content // Nachricht an WebSocket-Client senden
  }
}
