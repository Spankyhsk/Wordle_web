package services.JsonWrapper

import de.htwg.se.wordle.model.gamefieldComponent.GamefieldInterface
import play.api.*
import play.api.libs.json.*

import javax.inject.*
@Singleton
class JSONWrapper @Inject() () extends JSONWrapperInterface {

  def gameboardToJson(gameboardMap:Map[Int, GamefieldInterface[String]]):JsObject={
    
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
