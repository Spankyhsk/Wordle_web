package services.gameService

trait GameServiceInterface {
  def transformInput(input: String): Boolean

  def processInput(input: String): Boolean

  def endGame(input: String): String

  def startGame(input: Int): Boolean
}
