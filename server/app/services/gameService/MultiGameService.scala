package services.gameService

import de.htwg.se.wordle.model.gamefieldComponent.GamefieldInterface

import javax.inject.{Inject, Singleton}

@Singleton
class MultiGameService @Inject()(controll: de.htwg.se.wordle.controller.ControllerInterface) extends GameServiceInterface{

  var counter = 0
  def transformInput(input: String): Int = {
    val guess = controll.GuessTransform(input)
    if (controll.controllLength(guess.length()) && controll.controllRealWord(guess) && processInput(guess)) {
      if (controll.areYouWinningSon(guess)) {
        //nächste Runde
        counter = counter +1
        println("Runde gewonnen")
        2
      } else {
        //Spiel ist zu ende
        1
      }
    } else {
      //Nochmal raten
      0
    }
  }

  def processInput(input: String): Boolean = {
    if (!controll.areYouWinningSon(input) && controll.count()) {
      controll.set(controll.getVersuche(), controll.evaluateGuess(input))
      controll.setVersuche(controll.getVersuche() + 1)
      false
    } else {
      true
    }
  }

  def endGame(input: String): String = {
    "%d".format(counter)
  }

  def startGame(input: Int): Boolean = {
    controll.setVersuche(1)
    controll.changeState(input)
    controll.createGameboard()
    controll.createwinningboard()
    true
  }

  def getGameboard(): Map[Int, GamefieldInterface[String]] = {
    controll.getGameboard().getMap()
  }

  def getTargetWord(): String = {
    controll.getTargetword().values.mkString(", ")
  }
}
