package services.JsonWrapper

import play.api.libs.json._
import de.htwg.se.wordle.model.gamefieldComponent.GamefieldInterface

trait JSONWrapperInterface {
  def gameboardToJson(gameboardMap: Map[Int, GamefieldInterface[String]]): JsObject
}
