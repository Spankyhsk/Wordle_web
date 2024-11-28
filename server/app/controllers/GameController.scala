package controllers

import org.apache.pekko.actor.{ActorRef, ActorSystem}
import org.apache.pekko.stream.Materializer

import javax.inject.{Inject, Singleton}
import play.api.*
import play.api.mvc.*
import actors.{ChatActor, ChatSessionActor, PlayerActor}
import org.apache.pekko.pattern.ask
import org.apache.pekko.util.Timeout
import org.apache.pekko.stream.scaladsl.Flow
import scala.concurrent.duration.*

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.{JsError, JsObject, JsSuccess, JsValue, Json, OFormat}

import scala.collection.concurrent.TrieMap

@Singleton
class GameController @Inject()(cc: ControllerComponents, system: ActorSystem)(implicit mat: Materializer, ec: scala.concurrent.ExecutionContext) extends AbstractController(cc){

  implicit val timeout: Timeout = Timeout(10.seconds)
  var playerActors: TrieMap[String, ActorRef] = TrieMap()
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
          case "solo" => Ok(Json.obj("status" -> "success", "message" -> message, "mode" -> "solo")).withSession("userId" -> userId)
          case "multi" => Ok(Json.obj("status" -> "success", "message" -> "Game started in multiplayer mode", "mode" -> "multi")).withSession("userId" -> userId)
          case _ => BadRequest(Json.obj("status" -> "error", "message" -> "Invalid mode"))
        }
      case unexpected =>
        println(s"Unexpected Actor Response: $unexpected")
        InternalServerError(Json.obj("status" -> "error", "message" -> "Unexpected error during game start"))
    }.recover {
      case ex: Exception =>
        println(s"Error during game start: ${ex.getMessage}")
        InternalServerError(Json.obj("status" -> "error", "message" -> s"Error during game start: ${ex.getMessage}"))
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
        (playerActor ? PlayerActor.GetStopMessage()).mapTo[String].map { stopMessage =>
          stopPlayerActor(userId) // Actor stoppen und entfernen
          Ok(Json.obj("status" -> "success", "message" -> stopMessage))
        }.recover {
          case _: Exception => InternalServerError(Json.obj("status" -> "error", "message" -> "Error stopping the game"))
        }
      case None =>
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> "No game found to stop")))
    }
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
          case _: Exception => InternalServerError(Json.obj("status" -> "error", "message" -> "Error fetching gameboard"))
        }
      case None =>
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> "No game found")))
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
            val mode = awaitGetMode(playerActor) // Hole den Modus aus dem Actor
            (playerActor ? PlayerActor.MakeMove(value)).mapTo[PlayerActor.GameStatus].map { gameStatus =>
              gameStatus.status match {
                case "nextTurn" => Ok(Json.obj("status" -> "nextTurn"))
                case "gameover" => Ok(Json.obj("status" -> "gameover", "message" -> gameStatus.message.getOrElse("")))
                case "nextRound" => Ok(Json.obj("status" -> "nextRound"))
                case _ => BadRequest(Json.obj("status" -> "error", "message" -> "Unbekannter Status"))
              }
            }.recover {
              case _: Exception => InternalServerError(Json.obj("status" -> "error", "message" -> "Error processing move"))
            }
          case None =>
            Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> "No game found")))
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
        val mode = awaitGetMode(playerActor) // Hole den Modus aus dem Actor
        (playerActor ? PlayerActor.EndGame(input)).mapTo[String].map { resultMessage =>
          stopPlayerActor(userId) // Actor stoppen
          Ok(Json.obj("status" -> "success", "message" -> resultMessage))
        }.recover {
          case _: Exception => InternalServerError(Json.obj("status" -> "error", "message" -> "Error ending the game"))
        }
      case None =>
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> "No game found to end")))
    }
  }

  def nextRound(): Action[AnyContent] = Action.async { request =>
    val userId = request.session.get("userId").getOrElse("none")
    playerActors.get(userId) match {
      case Some(playerActor) =>
        (playerActor ? PlayerActor.nextRound()).map {
          case _ =>
            Ok(Json.obj("status" -> "nextRound"))
        }.recover {
          case ex: Exception =>
            println(s"Error during game start: ${ex.getMessage}")
            InternalServerError(Json.obj("status" -> "error", "message" -> s"Error starting next round: ${ex.getMessage}"))
        }
      case None =>
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> "No game found to start next round")))
    }
  }

  //private

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
}
