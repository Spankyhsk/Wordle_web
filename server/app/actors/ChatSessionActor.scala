package actors

import org.apache.pekko.actor.{Actor, ActorRef, Props}
import actors.ChatMessages._
import actors.ChatSessionActor._
import play.api.libs.json._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import play.api.libs.json.{Format, Json}

object ChatSessionActor {
  case class ClientMessage(message: String)

  case class InitMessage(userId: String)

  case class SessionTimeout()

  // JSON-Formate für Play JSON
  implicit val clientMessageFormat: Format[ClientMessage] = Json.format[ClientMessage]
  implicit val initMessageFormat: Format[InitMessage] = Json.format[InitMessage]
  implicit val sessionTimeoutFormat: Format[SessionTimeout] = Json.format[SessionTimeout]

  def props(out: ActorRef, chatActor: ActorRef): Props =
    Props(new ChatSessionActor(out, chatActor))
}

class ChatSessionActor(out: ActorRef, chatActor: ActorRef) extends Actor {

  import ChatSessionActor._
  import context.dispatcher

  private var userId: Option[String] = None

  // Timeout-Überwachung
  private var inactivityScheduler = context.system.scheduler.scheduleOnce(30.minutes, self, SessionTimeout())

  override def preStart(): Unit = {
    chatActor ! Join(self)
  }

  override def postStop(): Unit = {
    inactivityScheduler.cancel() // Scheduler stoppen, wenn Actor beendet wird
    chatActor ! Leave(self)
  }

  def receive: Receive = {
    case rawMessage: String =>
      println(s"Received raw message: $rawMessage")
      handleMessage(rawMessage)
      resetInactivityTimeout() // Timeout zurücksetzen bei neuer Nachricht

    case BroadcastMessage(content) =>
      println(s"Broadcasting message: $content")
      out ! content

    case SessionTimeout() =>
      println("Inactivity timeout reached. Sending timeout signal to frontend.")
      val timeoutMessage = Json.stringify(Json.toJson(SessionTimeout()))
      out ! timeoutMessage
      context.stop(self) // Actor stoppen, nachdem Timeout-Signal gesendet wurde
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

  private def resetInactivityTimeout(): Unit = {
    inactivityScheduler.cancel() // Aktuellen Scheduler stoppen
    inactivityScheduler = context.system.scheduler.scheduleOnce(30.minutes, self, SessionTimeout()) // Neuen Scheduler starten
  }
}
