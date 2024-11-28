package controllers


import actors.PlayerActor._
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import com.google.inject._

import javax.inject.{Inject, Singleton}
import javax.inject.*
import play.api.*
import play.api.mvc.*
import actors.PlayerActor



import scala.concurrent.ExecutionContext





import scala.util.{Failure, Success, Try}


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class WordleController @Inject()(cc: ControllerComponents, system: ActorSystem)(implicit mat: Materializer, ec: scala.concurrent.ExecutionContext) extends AbstractController(cc) {
  
  
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
   * Path:GET /solo
   * */
  def soloplayer() = Action {
    Ok.sendFile(new java.io.File("public/dist/index.html"))
  }
  
  /**
   * Methode um die keyboard zurückzugeben
   *
   * GET /keyboard
   * */
  def getKeyboard = Action {
    Ok(views.html.keyboard())
  }
  
  /**
   * Endgame
   *
   * GET /MULTI
   * */
  def multiplayer(): Action[AnyContent] = Action { request =>
    Ok.sendFile(new java.io.File("public/dist/index.html"))
  }
  
}

