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

  def userInputforTUI(userInput:String) = Action { implicit request: Request[AnyContent] =>
    tui.processInput(userInput)
    Ok(controll.toString)
  }
  
  def game() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.wordle())
  }

//  def handleString(yourString: String): Action[AnyContent] = Action {
//    Ok(s"Du hast diesen String übergeben: $yourString")
//  }




}
