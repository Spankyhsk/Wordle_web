package actors

import org.apache.pekko.actor.{Actor, ActorLogging, Props}
import services.gameService.{GameServiceInterface, MultiGameService, SoloGameService}
import PlayerActor.*
import com.google.inject.{Guice, Injector}
import de.htwg.se.wordle.WordleModuleJson
import de.htwg.se.wordle.controller.ControllerInterface
import de.htwg.se.wordle.model.gamefieldComponent.GamefieldInterface
import org.scalactic.TypeCheckedTripleEquals.convertToCheckingEqualizer
import play.api.libs.json.{JsObject, JsValue, Json}


object PlayerActor {

  case class StartGame(difficulty: Int)

  case class MakeMove(input: String)

  case class GameStatus(status: String, message: Option[String] = None)

  case class EndGame(result: String)

  case class GetStopMessage()

  case class GetGameboard()
  
  case class GetMode()

  case class PlayerState(userId: String, mode:String)

  case class nextRound()

  def props(userId: String, mode:String, name:String): Props = {
    val injector: Injector = Guice.createInjector(new WordleModuleJson)
    val controll: ControllerInterface = injector.getInstance(classOf[ControllerInterface])
    mode match{
      case "multi" => Props(new PlayerActor(new MultiGameService(controll), userId, mode, name))
      case _ => Props(new PlayerActor(new SoloGameService(controll), userId, mode, name))
    }
    
  }
}

class PlayerActor(gameService: GameServiceInterface, userId: String, mode: String, name:String) extends Actor with ActorLogging {

  val state: PlayerState = PlayerState(userId, mode)

  val userName:String = name

  def receive: Receive = {
    case StartGame(difficulty) =>
      gameService.startGame(difficulty)
      log.info(s"Spiel gestartet für Benutzer ${state.userId} im Modus ${state.mode}")
      sender() ! s"Spiel für Spieler gestartet mit Schwierigkeitsgrad $difficulty."

    case MakeMove(input) =>
      //val gameOver = gameService.transformInput(input)
      gameService.transformInput(input) match{
        case 1 =>
          log.info(s"Spiel für Spieler  beendet.")
          if(state.mode == "multi"){
            //val score = gameService.endGame(input).toInt
            //val jsonString = s"""{"name": "$userName", "score": $score}"""
            sender() ! GameStatus("gameover", Some(input))
          }else{
            sender() ! GameStatus("gameover", Some(input))
          }

        case 2 =>
          log.info(s"Spiel für Spieler nächste Runde")
          sender() ! GameStatus("nextRound", None)
        case _ =>
          log.info(s"Spiel für Spieler  fortgesetzt.")
          sender() ! GameStatus("nextTurn", None)

      }
    case EndGame(input) =>
      if(state.mode == "multi"){
        sender() ! gameService.endGame(userName)
      }else{
        sender() ! gameService.endGame(input)

      }

    case GetStopMessage() =>
      val stopMessage = s"Verloren! Lösungswort ist ${gameService.getTargetWord()}! Zum erneuten Spiel Schwierigkeit aussuchen."
      sender() ! stopMessage // Rückgabe des Abbruch-Textes

    case GetGameboard() =>
      sender() ! gameboardToJson(gameService.getGameboard())
      
    case GetMode =>
      sender() ! state.mode

    case nextRound =>
      sender() ! gameService.startGame(1)
  }

  private def gameboardToJson(gameboardMap: Map[Int, GamefieldInterface[String]]): JsObject = {

    Json.obj(
      "gameboard" -> Json.toJson(
        for {
          key <- 1 until gameboardMap.size + 1
        } yield {
          Json.obj(
            "key" -> key,
            "gamefield" -> gameboardMap(key).getMap()
          )
        }
      )
    )
  }
}
