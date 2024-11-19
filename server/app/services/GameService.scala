package services

import javax.inject.{Inject, Singleton}
import de.htwg.se.wordle.controller.ControllerInterface

@Singleton
class GameService @Inject() (controll: de.htwg.se.wordle.controller.ControllerInterface){

  def transformInput(input: String): Boolean = {
    var bool: Boolean = false
    val guess = controll.GuessTransform(input)
    if (controll.controllLength(guess.length()) && controll.controllRealWord(guess)){
      bool = processInput(guess)
    }
    bool
  }

  def processInput(input: String): Boolean = {
    var win: Boolean = false
    if (!controll.areYouWinningSon(input) && controll.count()) {
      controll.set(controll.getVersuche(), controll.evaluateGuess(input))
      controll.setVersuche(controll.getVersuche() + 1)
      return win
    } else {
      win = true
    }
    win
  }

  def endGame(input:String):String ={
    if (controll.areYouWinningSon(input)) {
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
    val start = true
    start
  }
}
