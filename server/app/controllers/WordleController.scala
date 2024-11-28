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

// Case-Klasse für einen Scoreboard-Eintrag
case class ScoreboardEntry(position: Int, name: String, score: Int)

object ScoreboardEntry {
  implicit val format: OFormat[ScoreboardEntry] = Json.format[ScoreboardEntry]
}
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
  def createPlayerActor(userId: String, mode: String, name: String): ActorRef = {
    val playerActor = system.actorOf(PlayerActor.props(userId, mode, name), s"playerActor-$userId")
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
  def newgame(input: Int, mode: String, name: String) = Action.async { implicit request => // no name => "anonym"
    // Wenn keine User-ID vorhanden ist, generiere eine neue zufällige ID und speichere sie in der Session
    val userId = request.session.get("userId").getOrElse {
      val newUserId = java.util.UUID.randomUUID().toString
      newUserId
    }
    println(request.session.get("userId"))

    println(s"Starting new game for UserId: $userId with input: $input and mode: $mode")

    val playerActor = playerActors.getOrElse(userId, createPlayerActor(userId, mode, name))

    (playerActor ? PlayerActor.StartGame(input)).map {
      case message: String =>
        println(s"Actor Response: $message")
        mode match {
          case "solo"  => Ok(views.html.wordle(true, message)).withSession("userId" -> userId)
          case "multi" => Ok(views.html.wordleMulti(true)).withSession("userId" -> userId)
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
    val userId = request.session.get("userId").getOrElse("none")
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
    val userId = request.session.get("userId").getOrElse("none")
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
    val userId = request.session.get("userId").getOrElse("none")
    val input = (request.body \ "input").asOpt[String]

    input match {
      case Some(value) =>
        playerActors.get(userId) match {
          case Some(playerActor) =>
            val mode = awaitGetMode(playerActor)  // Hole den Modus aus dem Actor
            (playerActor ? PlayerActor.MakeMove(value)).mapTo[PlayerActor.GameStatus].map { gameStatus =>
              gameStatus.status match {
                case "nextTurn" => Ok(Json.obj("status" -> "nextTurn"))
                case "gameover" => Ok(Json.obj("status" -> "gameover" ,"message" -> gameStatus.message.getOrElse("")))
                case "nextRound" => Ok(Json.obj("status" -> "nextRound"))
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
    val userId = request.session.get("userId").getOrElse("none")
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

  def nextRound(): Action[AnyContent] = Action.async { request =>
    val userId = request.session.get("userId").getOrElse("none")
    playerActors.get(userId) match {
      case Some(playerActor) =>
        (playerActor ? PlayerActor.nextRound()).map {
          case _ =>
            Ok(views.html.wordleMulti(true))
        }.recover {
          case ex: Exception =>
            println(s"Error during game start: ${ex.getMessage}")
            InternalServerError("Fehler beim Starten des Spiels")
        }
      case None =>
        Future.successful(BadRequest("Kein Spiel gefunden, das beendet werden kann."))
    }
  }



  /**
   * Websocket für chat
   *
   *GET /chat
   * */
  def chatSocket: WebSocket = WebSocket.accept[String, String] { _ =>
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

  // Hilfsfunktion: JSON-Datei lesen
  private def readScoreboard(): Try[Seq[ScoreboardEntry]] = Try {
    val jsonString = new String(Files.readAllBytes(filePath))
    val json = Json.parse(jsonString)
    (json \ "scoreboard").as[Seq[ScoreboardEntry]]
  }

  // Hilfsfunktion: JSON-Datei schreiben
  private def writeScoreboard(scoreboard: Seq[ScoreboardEntry]): Try[Unit] = Try {
    val json = Json.obj("scoreboard" -> scoreboard)
    Files.write(filePath, Json.prettyPrint(json).getBytes, StandardOpenOption.TRUNCATE_EXISTING)
  }

  /**
   * gibt Scoreboard
   *
   * POST /scoreboard
   * */
  def updateScoreboard: Action[JsValue] = Action(parse.json) { request =>
    // Validierung des neuen Eintrags
    request.body.validate[ScoreboardEntry] match {
      case JsSuccess(newEntry, _) =>
        readScoreboard() match {
          case Success(scoreboard) =>
            val updatedScoreboard = updateRanking(scoreboard, newEntry)

            // Schreibe die Änderungen zurück in die Datei
            writeScoreboard(updatedScoreboard) match {
              case Success(_) => Ok(Json.obj("status" -> "success", "message" -> "Scoreboard updated"))
              case Failure(ex) => InternalServerError(Json.obj("status" -> "error", "message" -> ex.getMessage))
            }

          case Failure(ex) =>
            InternalServerError(Json.obj("status" -> "error", "message" -> s"Failed to read scoreboard: ${ex.getMessage}"))
        }

      case JsError(errors) =>
        BadRequest(Json.obj("status" -> "error", "message" -> "Invalid JSON", "details" -> errors.toString))
    }
  }

  // Hilfsfunktion: Aktualisiere das Ranking
  private def updateRanking(scoreboard: Seq[ScoreboardEntry], newEntry: ScoreboardEntry): Seq[ScoreboardEntry] = {
    // Neuen Eintrag hinzufügen, nach Score sortieren, Positionen aktualisieren
    val updated = (scoreboard :+ newEntry)
      .sortBy(-_.score) // Nach Score absteigend sortieren
      .zipWithIndex // Index für Position verwenden
      .map { case (entry, index) => entry.copy(position = index + 1) } // Positionen neu setzen

    updated.take(5) // Nur die Top 5 behalten
  }


}

