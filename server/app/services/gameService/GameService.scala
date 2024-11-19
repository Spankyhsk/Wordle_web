package services.gameService

import de.htwg.se.wordle.controller.ControllerInterface

import javax.inject.{Inject, Singleton}

@Singleton
class GameService @Inject() (controll: de.htwg.se.wordle.controller.ControllerInterface) extends GameServiceInterface {

  def transformInput(input: String): Boolean = {
    val guess = controll.GuessTransform(input)
    controll.controllLength(guess.length()) && controll.controllRealWord(guess) && processInput(guess)
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

  def endGame(input:String):String ={
    if (controll.areYouWinningSon(controll.GuessTransform(input))) {
      "Gewonnen Lösungswort ist: " + controll.getTargetword().values.mkString(", ") + "! Zum erneuten Spiel Schwierigkeit aussuchen"
    } else {
      "Verloren! Lösungswort ist " + controll.getTargetword().values.mkString(", ") + "! Zum erneuten Spiel Schwierigkeit aussuchen"
    }
  }

  def startGame(input:Int):Boolean = {
    controll.setVersuche(1)
    controll.changeState(input)
    controll.createGameboard()
    controll.createwinningboard()
    true
  }
}
