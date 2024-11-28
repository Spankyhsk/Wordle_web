package actors

import org.apache.pekko.actor.{Actor, ActorRef, Props}
import actors.ChatMessages._
import actors.ChatSessionActor._
import play.api.libs.json._



object ChatSessionActor {
  case class ClientMessage(message: String)
  case class InitMessage(userId: String)

  // JSON-Formate für Play JSON
  implicit val clientMessageFormat: Format[ClientMessage] = Json.format[ClientMessage]
  implicit val initMessageFormat: Format[InitMessage] = Json.format[InitMessage]

  def props(out: ActorRef, chatActor: ActorRef): Props =
    Props(new ChatSessionActor(out, chatActor))
}

class ChatSessionActor(out: ActorRef, chatActor: ActorRef) extends Actor {
  private var userId: Option[String] = None
  override def preStart(): Unit = {
    chatActor ! Join(self)
  }

  override def postStop(): Unit = {
    chatActor ! Leave(self)
  }

  def receive: Receive = {
    case rawMessage: String =>
      println(s"Received raw message: $rawMessage")
      handleMessage(rawMessage)

    case BroadcastMessage(content) =>
      println(s"Broadcasting message: $content")
      out ! content
  }

  private def handleMessage(rawMessage: String): Unit = {
    val parsedJson = Json.parse(rawMessage)
    (parsedJson \ "type").asOpt[String] match {
      case Some("init") =>
        userId = (parsedJson \ "userId").asOpt[String]
        println(s"User initialized with ID: ${userId.getOrElse("unknown")}")

      case Some("message") =>
        val content = (parsedJson \ "content").asOpt[String].getOrElse("")
        userId.foreach { id =>
          val message = Json.stringify(Json.toJson(ChatMessages.ChatMessage(id, content)))
          chatActor ! BroadcastMessage(message)
        }

      case _ =>
        println("Unknown message type received")
    }
  }



}