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
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

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

  def play(userInput:String) = Action { implicit request: Request[AnyContent] =>
    tui.processInput(userInput)
    Ok(controll.toString)
  }




}
