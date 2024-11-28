package controllers


import actors.PlayerActor.{EndGame, GetGameboard}
import org.apache.pekko.actor.{ActorRef, ActorSystem}
import org.apache.pekko.stream.Materializer
import com.google.inject.{Guice, Injector}
import de.htwg.se.wordle.WordleModuleJson

import javax.inject.*
import play.api.*
import play.api.mvc.*
import de.htwg.se.wordle.controller.ControllerInterface
import play.api.libs.json.{JsError, JsObject, JsSuccess, JsValue, Json, OFormat}
import services.gameService.{GameServiceInterface, SoloGameService}
import actors.{ChatActor, ChatSessionActor, PlayerActor}
import org.apache.pekko.stream.scaladsl.Flow
import org.apache.pekko.util.Timeout
import play.api.libs.streams.ActorFlow

import scala.collection.concurrent.TrieMap
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.*
import org.apache.pekko.pattern.ask
import org.apache.pekko.util.Timeout

import java.nio.file.{Files, Paths, StandardOpenOption}
import java.util.UUID
import scala.util.{Failure, Success, Try}


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class WordleController @Inject()(cc: ControllerComponents, system: ActorSystem)(implicit mat: Materializer, ec: scala.concurrent.ExecutionContext) extends AbstractController(cc) {
  
  
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
  
  /**
   * Call game area
   *
   * Path:GET /solo
   * */
  def soloplayer() = Action {
    Ok(views.html.wordle(false, "Wähle die Schwierigkeit aus!"))
  }
  
  /**
   * Methode um die keyboard zurückzugeben
   *
   * GET /keyboard
   * */
  def getKeyboard = Action {
    Ok(views.html.keyboard())
  }
  
  /**
   * Endgame
   *
   * GET /MULTI
   * */
  def multiplayer(): Action[AnyContent] = Action { request =>
    Ok(views.html.wordleMulti(false)).withSession("gameMode" -> "multi")
  }
  
}

