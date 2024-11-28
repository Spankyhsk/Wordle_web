package actors

import org.apache.pekko.actor.ActorRef
import play.api.libs.json._

object ChatMessages {
  case class BroadcastMessage(content: String)

  case class Join(ref: ActorRef)

  case class Leave(ref: ActorRef)
  

  case class ChatMessage(senderId: String, message: String)

  // JSON-Format für Play JSON
  implicit val chatMessageFormat: Format[ChatMessage] = Json.format[ChatMessage]
}
