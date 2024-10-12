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

  def play = Action { implicit request: Request[AnyContent] =>
    val injector = Guice.createInjector(new WordleModuleJson)
    val controll = injector.getInstance(classOf[ControllerInterface])
    val tui = new TUI(controll)

    // Beispiel für die TUI-Interaktion
    println("Willkommen zu Wordle")
    while (true) {
      if (tui.getnewgame()) {
        println("Gamemode aussuchen: \n1:= leicht\n2:= mittel\n3:= schwer")
      }
      tui.processInput(StdIn.readLine())
    }
    Ok("Das Spiel läuft!") // Dies wird nicht erreicht, da die Schleife endlos ist
  }




}
