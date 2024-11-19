package controllers

import com.google.inject.{Guice, Injector}
import de.htwg.se.wordle.WordleModuleJson

import javax.inject.*
import play.api.*
import play.api.mvc.*
import de.htwg.se.wordle.controller.ControllerInterface
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import services.{GameService, JSONWrapper}

import scala.io.StdIn

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class WordleController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  val injector: Injector = Guice.createInjector(new WordleModuleJson)
  val controll: ControllerInterface = injector.getInstance(classOf[ControllerInterface])
  val jsonWrapper: JSONWrapper = new JSONWrapper
  val gameService: GameService = new GameService(controll)


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
   * process Input
   *
   * Path:GET /play/:input
   *
  def gameinput(input:String) = Action {

    var bool = true
    var message = "Geben Sie um zu raten bitte ein fünfstelliges Wort ein!"
    input match {
      case "$wrong" => {
        message = "Falsche Eingabe! Geben Sie um zu raten bitte ein fünfstelliges Wort ein!"
      }
      case default =>{
        if (!controll.areYouWinningSon(input) && controll.count()) {
          controll.set(controll.getVersuche(), controll.evaluateGuess(input))
          controll.setVersuche(controll.getVersuche() + 1)
        } else {
          bool = false
          if (controll.areYouWinningSon(input)) {
            message = "Gewonnen Lösungswort ist: " + controll.getTargetword().values.mkString(", ") + "! Zum erneuten Spiel Schwierigkeit aussuchen"
          } else {
            message = "Verloren! Lösungswort ist " + controll.getTargetword().values.mkString(", ") + "! Zum erneuten Spiel Schwierigkeit aussuchen"
          }
        }
      }
    }

    Ok(views.html.wordle(controll, bool, message))
  }
   */

  /**
   *
   *
   * Path: GET /play
   *
  def redirectToGame(): Action[AnyContent] = Action { implicit request =>
    request.getQueryString("input") match {
      case Some(input) if input.length == 5 =>
        val guess = controll.GuessTransform(input)
        if(controll.controllLength(guess.length) && controll.controllRealWord(guess)){
          Redirect(routes.WordleController.gameinput(guess)) // Weiterleitung zur gameinput-Methode
        }else{
          Redirect(routes.WordleController.gameinput("$wrong"))
        }
      case _ =>
        BadRequest("Bitte genau 5 Buchstaben eingeben.")
    }
  }
  */

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
   *
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
          Ok(Json.obj("status" -> "success")) // Erfolgreiche Verarbeitung
        } else {
          Ok(Json.obj("status" -> "gameover"))
        }
      case None =>
        // Fehler, wenn keine Eingabe vorhanden ist
        BadRequest(Json.obj("status" -> "error", "message" -> "Keine Eingabe erhalten"))
    }
  }

  def testInput(): Action[AnyContent] = Action { request =>
    Ok(Json.obj("status" -> "success", "message" -> "Request erfolgreich"))
  }
  
  def getWinning(): Action[AnyContent] = Action { request =>
    Ok(views.html.wordle(controll, false, message = "Spiel zu Ende"))
  }

}
