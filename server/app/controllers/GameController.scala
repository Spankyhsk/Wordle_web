package controllers

import actors.PlayerActor
import org.apache.pekko.actor.{ActorRef, ActorSystem}
import org.apache.pekko.stream.Materializer
import org.apache.pekko.util.Timeout
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import javax.inject.{Inject, Singleton}
import scala.collection.concurrent.TrieMap
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.*
import org.apache.pekko.pattern.ask

@Singleton
class GameController @Inject()(cc: ControllerComponents, system: ActorSystem)(implicit mat: Materializer, ec: ExecutionContext) extends AbstractController(cc) {
  implicit val timeout: Timeout = Timeout(10.seconds)
  var playerActors: TrieMap[String, ActorRef] = TrieMap()

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
        Ok(Json.obj(
          "status" -> "success",
          "message" -> message,
          "mode" -> mode
        )).withSession("userId" -> userId)
      case _ =>
        InternalServerError(Json.obj("status" -> "error", "message" -> "Fehler beim Starten des Spiels"))
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
        val mode = awaitGetMode(playerActor) // Hole den Modus aus dem Actor
        (playerActor ? PlayerActor.GetStopMessage()).mapTo[String].map { stopMessage =>
          stopPlayerActor(userId) // Actor stoppen und entfernen
          Ok(Json.obj("status" -> "success", "message" -> stopMessage))
        }.recover {_ =>
          InternalServerError(Json.obj("status" -> "error", "message" -> "Fehler beim Stoppen des Spiels"))
        }
      case None =>
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> "Kein Spiel gefunden.")))
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
          Ok(Json.obj(
            "status" -> "success",
            "data" -> gameboardJson
          ))
        }.recover {
          case _: Exception => InternalServerError(Json.obj(
            "status" -> "error",
            "message" -> "Fehler beim Abrufen des Gameboards"
          ))
        }
      case None =>
        Future.successful(BadRequest(Json.obj(
          "status" -> "error",
          "message" -> "Kein Spiel gefunden."
        )))
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
              case _: Exception => InternalServerError(Json.obj("status" -> "error", "message" -> "Fehler bei der Verarbeitung der Eingabe"))
            }
          case None =>
            Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> "Kein Spiel gefunden.")))
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
          mode match {
            case "solo" =>
              Ok(Json.obj(
                "status" -> "success",
                "message" -> resultMessage,
                "mode" -> "solo"
              ))
            case "multi" =>
              Ok(Json.obj(
                "status" -> "success",
                "message" -> resultMessage,
                "mode" -> "multi"
              ))
            case _ =>
              BadRequest(Json.obj(
                "status" -> "error",
                "message" -> "Modus existiert nicht"
              ))
          }
        }.recover {
          case _: Exception => InternalServerError(Json.obj(
            "status" -> "error",
            "message" -> "Fehler beim Beenden des Spiels"
          ))
        }
      case None =>
        Future.successful(BadRequest(Json.obj(
          "status" -> "error",
          "message" -> "Kein Spiel gefunden, das beendet werden kann."
        )))
    }
  }

  def nextRound(): Action[AnyContent] = Action.async { request =>
    val userId = request.session.get("userId").getOrElse("none")
    playerActors.get(userId) match {
      case Some(playerActor) =>
        (playerActor ? PlayerActor.nextRound()).map { _ =>
          Ok(Json.obj(
            "status" -> "success",
            "message" -> "Nächste Runde gestartet"
          ))
        }.recover {
          case ex: Exception =>
            println(s"Error during game start: ${ex.getMessage}")
            InternalServerError(Json.obj(
              "status" -> "error",
              "message" -> "Fehler beim Starten der nächsten Runde"
            ))
        }
      case None =>
        Future.successful(BadRequest(Json.obj(
          "status" -> "error",
          "message" -> "Kein Spiel gefunden, das beendet werden kann."
        )))
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

}
