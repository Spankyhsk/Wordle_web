package services.gameService

import de.htwg.se.wordle.model.gamefieldComponent.GamefieldInterface

trait GameServiceInterface {
  def transformInput(input: String): Boolean

  def processInput(input: String): Boolean

  def endGame(input: String): String

  def startGame(input: Int): Boolean
  
  def getGameboard(): Map[Int, GamefieldInterface[String]]
  
  def getTargetWord():String 
}
