package controllers


import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.{Guice, Injector}
import de.htwg.se.wordle.WordleModuleJson

import javax.inject.*
import play.api.*
import play.api.mvc.*
import de.htwg.se.wordle.controller.ControllerInterface
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import services.JsonWrapper.{JSONWrapper, JSONWrapperInterface}
import services.gameService.{GameService, GameServiceInterface}
import actors.ChatActor

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class WordleController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext, system: ActorSystem, mat: Materializer) extends AbstractController(cc) {

  val injector: Injector = Guice.createInjector(new WordleModuleJson)
  val controll: ControllerInterface = injector.getInstance(classOf[ControllerInterface])
  val jsonWrapper: JSONWrapperInterface = new JSONWrapper
  val gameService: GameServiceInterface = new GameService(controll)
  private val chatRoomActor = system.actorOf(ChatActor.props, "chatActor")


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
   * Path:GET /start
   * */
  def game() = Action {
    Ok(views.html.wordle(controll, false, "Wähle die Schwierigkeit aus!"))
  }
  /**
   * Create a new game
   *
   * Path:GET /new/:input
   * */

  def newgame(input:Int) = Action {
    gameService.startGame(input)
    Ok(views.html.wordle(controll, true, "Geben Sie um zu raten bitte ein fünfstelliges Wort ein!"))
  }


  /**
   *
   *
   * Get /stop
   * */
  def stopGame(): Action[AnyContent] = Action {
    val message = "Verloren! Lösungswort ist " + controll.getTargetword().values.mkString(", ") + "! Zum erneuten Spiel Schwierigkeit aussuchen"
    Ok(views.html.wordle(controll, false, message))
  }
  
  /**
   * Damit Später vielicht das Spiel an und aus geschaltet werden kann
   * 
   * GET /end
   * */
  def backToRules(): Action[AnyContent] = Action{
    Ok(views.html.index())
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
   * Methode um gameboard als Json zu kriegen
   * 
   * GET /gameboard
   * */
  def getGameboard = Action {
    Ok(jsonWrapper.gameboardToJson(controll.getGameboard().getMap()))
  }

  /**
   * Post Input 
   *
   * POST /play
   * */

  def gameInput(): Action[JsValue] = Action(parse.tolerantJson) { request =>
    // Extrahiere das "input"-Feld aus dem JSON-Body
    val input = (request.body \ "input").asOpt[String]
    input match {
      case Some(value) =>
        // Überprüfe die Eingabe mithilfe des GameService
        if (!gameService.transformInput(value)) {
          Ok(Json.obj("status" -> "nextTurn")) // Erfolgreiche Verarbeitung
        } else {
          Ok(Json.obj("status" -> "gameover", "message" -> value))
        }
      case None =>
        // Fehler, wenn keine Eingabe vorhanden ist
        BadRequest(Json.obj("status" -> "error", "message" -> "Keine Eingabe erhalten"))
    }
  }
  
  /**
   * Endgame
   * 
   * GET /gameOver/:input
   * */
  def getWinning(input:String): Action[AnyContent] = Action { request =>
    Ok(views.html.wordle(controll, false, gameService.endGame(input)))
  }


}
