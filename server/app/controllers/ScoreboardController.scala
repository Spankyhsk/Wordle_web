package controllers

import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import java.nio.file.{Files, Paths, StandardOpenOption}
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsError, JsObject, JsSuccess, JsValue, Json, OFormat}

import scala.util.{Failure, Success, Try}

// Case-Klasse für einen Scoreboard-Eintrag
case class ScoreboardEntry(position: Int, name: String, score: Int)

object ScoreboardEntry {
  implicit val format: OFormat[ScoreboardEntry] = Json.format[ScoreboardEntry]
}

@Singleton
class ScoreboardController @Inject()(cc: ControllerComponents) extends AbstractController(cc){

  private val filePath = Paths.get("public/data/scoreboard.json")

  /**
   * gibt Scoreboard
   *
   * GET /scoreboard
   * */
  def getScoreboard: Action[AnyContent] = Action {
    val result = Try {
      val jsonString = new String(Files.readAllBytes(filePath)) // JSON-Datei lesen
      Json.parse(jsonString) // String in JSON umwandeln
    }

    result match {
      case Success(json) => Ok(json) // JSON zurückgeben
      case Failure(exception) =>
        InternalServerError(Json.obj("status" -> "error", "message" -> exception.getMessage))
    }
  }

  // Hilfsfunktion: JSON-Datei lesen
  private def readScoreboard(): Try[Seq[ScoreboardEntry]] = Try {
    val jsonString = new String(Files.readAllBytes(filePath))
    val json = Json.parse(jsonString)
    (json \ "scoreboard").as[Seq[ScoreboardEntry]]
  }

  // Hilfsfunktion: JSON-Datei schreiben
  private def writeScoreboard(scoreboard: Seq[ScoreboardEntry]): Try[Unit] = Try {
    val json = Json.obj("scoreboard" -> scoreboard)
    Files.write(filePath, Json.prettyPrint(json).getBytes, StandardOpenOption.TRUNCATE_EXISTING)
  }

  /**
   * gibt Scoreboard
   *
   * POST /scoreboard
   * */
  def updateScoreboard: Action[JsValue] = Action(parse.json) { request =>
    // Validierung des neuen Eintrags
    request.body.validate[ScoreboardEntry] match {
      case JsSuccess(newEntry, _) =>
        readScoreboard() match {
          case Success(scoreboard) =>
            val updatedScoreboard = updateRanking(scoreboard, newEntry)

            // Schreibe die Änderungen zurück in die Datei
            writeScoreboard(updatedScoreboard) match {
              case Success(_) => Ok(Json.obj("status" -> "success", "message" -> "Scoreboard updated"))
              case Failure(ex) => InternalServerError(Json.obj("status" -> "error", "message" -> ex.getMessage))
            }

          case Failure(ex) =>
            InternalServerError(Json.obj("status" -> "error", "message" -> s"Failed to read scoreboard: ${ex.getMessage}"))
        }

      case JsError(errors) =>
        BadRequest(Json.obj("status" -> "error", "message" -> "Invalid JSON", "details" -> errors.toString))
    }
  }

  // Hilfsfunktion: Aktualisiere das Ranking
  private def updateRanking(scoreboard: Seq[ScoreboardEntry], newEntry: ScoreboardEntry): Seq[ScoreboardEntry] = {
    // Neuen Eintrag hinzufügen, nach Score sortieren, Positionen aktualisieren
    val updated = (scoreboard :+ newEntry)
      .sortBy(-_.score) // Nach Score absteigend sortieren
      .zipWithIndex // Index für Position verwenden
      .map { case (entry, index) => entry.copy(position = index + 1) } // Positionen neu setzen

    updated.take(5) // Nur die Top 5 behalten
  }
}
