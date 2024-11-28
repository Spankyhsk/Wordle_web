package controllers

import actors.{ChatActor, ChatSessionActor}
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import play.api.mvc._

import javax.inject.{Inject, Singleton}

@Singleton
class ChatController @Inject()(cc: ControllerComponents, system: ActorSystem)(implicit mat: Materializer) extends AbstractController(cc){
  private val chatActor = system.actorOf(ChatActor.props, "chatActor")

  /**
   * Websocket für chat
   *
   * GET /chat
   * */
  def chatSocket: WebSocket = WebSocket.accept[String, String] { _ =>
    ActorFlow.actorRef { out =>
      println("WebSocket connection established")
      ChatSessionActor.props(out, chatActor)
    }(system, mat)
  }
}
