package controllers

import com.google.inject.Guice
import de.htwg.se.wordle.WordleModuleJson

import javax.inject.*
import play.api.*
import play.api.mvc.*
import de.htwg.se.wordle.aview.TUI
import de.htwg.se.wordle.controller.ControllerInterface


import scala.io.StdIn

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class WordleController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  val injector = Guice.createInjector(new WordleModuleJson)
  val controll = injector.getInstance(classOf[ControllerInterface])
  val tui = new TUI(controll)


  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }


  /**
   * Call game area
   *
   * Path:GET /start
   * */
  def game() = Action {
    Ok(views.html.wordle(controll, false))
  }

  /**
   * Create a new game
   *
   * Path:GET /new/:input
   * */

  def newgame(input:Int) = Action {
    controll.setVersuche(1)
    controll.changeState(input)
    controll.createGameboard()
    controll.createwinningboard()
    Ok(views.html.wordle(controll, true))
  }
  /**
   * process Input
   *
   * Path:GET /play/:input
   * */
  def gameinput(input:String) = Action {
    val guess = controll.GuessTransform(input)
    var bool = true
    if(controll.controllLength(guess.length) && controll.controllRealWord(guess)){
      if(!controll.areYouWinningSon(guess) && controll.count()){
        controll.set(controll.getVersuche(), controll.evaluateGuess(guess))
        controll.setVersuche(controll.getVersuche() + 1)
      }else{
        controll.set(controll.getVersuche(), controll.evaluateGuess(guess))
        bool = false
      }
    }
    //als output auf der Console
    println(tui)
    Ok(views.html.wordle(controll, bool))
  }

  def redirectToGame(): Action[AnyContent] = Action { implicit request =>
    request.getQueryString("input") match {
      case Some(input) if input.length == 5 =>
        Redirect(routes.WordleController.gameinput(input)) // Weiterleitung zur gameinput-Methode
      case _ =>
        BadRequest("Bitte genau 5 Buchstaben eingeben.")
    }
  }




}
