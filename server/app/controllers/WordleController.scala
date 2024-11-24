package controllers


import actors.PlayerActor.{EndGame, GetGameboard}
import org.apache.pekko.actor.{ActorRef, ActorSystem}
import org.apache.pekko.stream.Materializer
import com.google.inject.{Guice, Injector}
import de.htwg.se.wordle.WordleModuleJson

import javax.inject.*
import play.api.*
import play.api.mvc.{AnyContent, Request, request, *}
import de.htwg.se.wordle.controller.ControllerInterface
import play.api.libs.json.{JsObject, JsValue, Json}
import services.JsonWrapper.{JSONWrapper, JSONWrapperInterface}
import services.gameService.{GameService, GameServiceInterface}
import actors.{ChatActor, ChatSessionActor, PlayerActor}
import org.apache.pekko.stream.scaladsl.Flow
import org.apache.pekko.util.Timeout
import play.api.libs.streams.ActorFlow

import scala.collection.concurrent.TrieMap
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.*
import org.apache.pekko.pattern.ask
import org.apache.pekko.util.Timeout

import java.nio.file.{Files, Paths}
import scala.util.{Failure, Success, Try}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class WordleController @Inject()(cc: ControllerComponents, system: ActorSystem)(implicit mat: Materializer, ec: scala.concurrent.ExecutionContext) extends AbstractController(cc) {

  private val chatActor = system.actorOf(ChatActor.props, "chatActor")
  implicit val timeout: Timeout = Timeout(10.seconds)
  var playerActors: TrieMap[String, ActorRef] = TrieMap()
  private val filePath = Paths.get("public/data/scoreboard.json")

  // Methode zum Erstellen eines neuen Akteurs für einen Benutzer
  def createPlayerActor(userId: String): ActorRef = {
    val playerActor = system.actorOf(PlayerActor.props(), s"playerActor-$userId")
    playerActors.put(userId, playerActor)
    println("PlayerActor gestartet")
    playerActor
  }

  //Methode zum Schließen eines Akteurs für einen Benutzer
  def stopPlayerActor(userId: String): Unit = {
    playerActors.get(userId).foreach { actor =>
      system.stop(actor)
      playerActors.remove(userId)
      println("PlayerActor beendet")
    }
  }

  // Hilfsmethode, um den Modus aus dem PlayerActor zu holen
  def awaitGetMode(playerActor: ActorRef): String = {
    import scala.concurrent.ExecutionContext.Implicits.global
    import org.apache.pekko.pattern.ask
    implicit val timeout: Timeout = Timeout(5.seconds)

    // Die Antwort des Actors (der Modus) abfragen
    val future = (playerActor ? PlayerActor.GetMode).mapTo[String]
    scala.concurrent.Await.result(future, timeout.duration)
  }



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
   * Create a new game
   *
   * Path:GET /new/:input
   * */
  def newgame(input: Int, mode: String) = Action.async { implicit request =>
    val userId = request.session.get("userId").getOrElse("anonymous")
    println(s"Starting new game for UserId: $userId with input: $input and mode: $mode")

    val playerActor = playerActors.getOrElse(userId, createPlayerActor(userId))

    (playerActor ? PlayerActor.StartGame(input, mode)).map {
      case message: String =>
        println(s"Actor Response: $message")
        mode match {
          case "solo"  => Ok(views.html.wordle(true, message))
          case "multi" => Ok(views.html.wordleMulti(true))
          case _       => BadRequest("Modus existiert nicht")
        }
      case unexpected =>
        println(s"Unexpected Actor Response: $unexpected")
        InternalServerError("Fehler beim Verarbeiten der Antwort")
    }.recover {
      case ex: Exception =>
        println(s"Error during game start: ${ex.getMessage}")
        InternalServerError("Fehler beim Starten des Spiels")
    }
  }


  /**
   *
   *
   * Get /stop
   * */
  def stopGame(): Action[AnyContent] = Action.async { implicit request =>
    val userId = request.session.get("userId").getOrElse("anonymous")
    playerActors.get(userId) match {
      case Some(playerActor) =>
        val mode = awaitGetMode(playerActor)  // Hole den Modus aus dem Actor
        (playerActor ? PlayerActor.GetStopMessage()).mapTo[String].map { stopMessage =>
          stopPlayerActor(userId) // Actor stoppen und entfernen
          mode match {
            case "solo" => Ok(views.html.wordle(false, stopMessage))
            case "multi" => Ok(views.html.wordleMulti(false))
            case _ => BadRequest("Mode gibt es nicht")
          }

        }.recover {
          case _: Exception => InternalServerError("Fehler beim Stoppen des Spiels")
        }
      case None =>
        Future.successful(BadRequest("Kein Spiel gefunden, das gestoppt werden kann."))
    }
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
  def getGameboard = Action.async { implicit request =>
    val userId = request.session.get("userId").getOrElse("anonymous")
    playerActors.get(userId) match {
      case Some(playerActor) =>
        (playerActor ? PlayerActor.GetGameboard()).mapTo[JsObject].map { gameboardJson =>
          Ok(gameboardJson)
        }.recover {
          case _: Exception => InternalServerError("Fehler beim Abrufen des Gameboards")
        }
      case None =>
        Future.successful(BadRequest("Kein Spiel gefunden."))
    }
  }

  /**
   * Post Input 
   *
   * POST /play
   * */

  def gameInput(): Action[JsValue] = Action.async(parse.tolerantJson) { implicit request =>
    val userId = request.session.get("userId").getOrElse("anonymous")
    val input = (request.body \ "input").asOpt[String]

    input match {
      case Some(value) =>
        playerActors.get(userId) match {
          case Some(playerActor) =>
            val mode = awaitGetMode(playerActor)  // Hole den Modus aus dem Actor
            (playerActor ? PlayerActor.MakeMove(value)).mapTo[PlayerActor.GameStatus].map { gameStatus =>
              gameStatus.status match {
                case "nextTurn" => Ok(Json.obj("status" -> "nextTurn"))
                case "gameover" => Ok(Json.obj("status" -> "gameover", "message" -> gameStatus.message.getOrElse("")))
                case _          => BadRequest(Json.obj("status" -> "error", "message" -> "Unbekannter Status"))
              }
            }.recover {
              case _: Exception => InternalServerError("Fehler bei der Verarbeitung der Eingabe")
            }
          case None =>
            Future.successful(BadRequest("Kein Spiel gefunden."))
        }
      case None =>
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> "Keine Eingabe erhalten")))
    }
  }
  
  /**
   * Endgame
   * 
   * GET /gameOver/:input
   * */
  def getGameOver(input: String): Action[AnyContent] = Action.async { implicit request =>
    val userId = request.session.get("userId").getOrElse("anonymous")
    playerActors.get(userId) match {
      case Some(playerActor) =>
        val mode = awaitGetMode(playerActor)  // Hole den Modus aus dem Actor
        (playerActor ? PlayerActor.EndGame(input)).mapTo[String].map { resultMessage =>
          stopPlayerActor(userId) // Actor stoppen
          mode match {
            case "solo" => Ok(views.html.wordle(false, resultMessage))
            case "multi" => Ok(views.html.wordleMulti(false))
            case _ => BadRequest("Mode gibt es nicht")
          }
        }.recover {
          case _: Exception => InternalServerError("Fehler beim Beenden des Spiels")
        }
      case None =>
        Future.successful(BadRequest("Kein Spiel gefunden, das beendet werden kann."))
    }
  }

  /**
   * Endgame
   *
   * GET /MULTI
   * */
  def multiplayer(): Action[AnyContent] = Action { request =>
    Ok(views.html.wordleMulti(false)).withSession("gameMode" -> "multi")
  }

  /**
   * Websocket für chat
   *
   *GET /chat
   * */
  def chatSocket: WebSocket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { out =>
      println("WebSocket connection established")
      ChatSessionActor.props(out, chatActor)
    }(system, mat)
  }

  /**
   * gibt Scoreboard
   *
   * GET /scoreboard
   * */
  def getScoreboard: Action[AnyContent] = Action {
    val result = Try {
      val jsonString = new String(Files.readAllBytes(filePath)) // JSON-Datei lesen
      Json.parse(jsonString) // String in JSON umwandeln
    }

    result match {
      case Success(json) => Ok(json) // JSON zurückgeben
      case Failure(exception) =>
        InternalServerError(Json.obj("status" -> "error", "message" -> exception.getMessage))
    }
  }


}

