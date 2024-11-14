package services

import play.api.*
import javax.inject.*
import play.api.libs.json._
import de.htwg.se.wordle.model.gamefieldComponent.GamefieldInterface
@Singleton
class JSONWrapper @Inject() (){

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
