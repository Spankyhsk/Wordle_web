package controllers

import play.api.libs.json.{JsError, JsSuccess, JsValue, Json, OFormat}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import java.nio.file.{Files, Paths, StandardOpenOption}
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

// Case-Klasse für einen Scoreboard-Eintrag
case class ScoreboardEntry(position: Option[Int], name: String, score: Int)


object ScoreboardEntry {
  implicit val scoreboardEntryFormat: OFormat[ScoreboardEntry] = Json.format[ScoreboardEntry]
}

@Singleton
class ScoreboardController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc){
  private val filePath = Paths.get("public/data/scoreboard.json")

  /**
   * gibt Scoreboard
   *
   * GET /scoreboard
   * */
  def getScoreboard: Action[AnyContent] = Action {
    Try {
      val jsonString = new String(Files.readAllBytes(filePath))
      Json.parse(jsonString)
    } match {
      case Success(json) => Ok(Json.obj("status" -> "success", "scoreboard" -> json))
      case Failure(ex) => InternalServerError(Json.obj("status" -> "error", "message" -> ex.getMessage))
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

  // Hilfsfunktion: Aktualisiere das Ranking
  private def updateRanking(scoreboard: Seq[ScoreboardEntry], newEntry: ScoreboardEntry): Seq[ScoreboardEntry] = {
    // Neuen Eintrag hinzufügen, nach Score sortieren, Positionen aktualisieren
    val updated = (scoreboard :+ newEntry)
      .sortBy(-_.score) // Nach Score absteigend sortieren
      .zipWithIndex // Index für Position verwenden
      .map { case (entry, index) => entry.copy(position = Some(index + 1)) } // Positionen neu setzen

    updated.take(5) // Nur die Top 5 behalten
  }



  /**
   * gibt Scoreboard
   *
   * POST /scoreboard
   * */
  // Endpunkt zum Aktualisieren des Scoreboards
  def updateScoreboard: Action[JsValue] = Action(parse.json) { request =>
    // Validierung des neuen Eintrags
    request.body.validate[ScoreboardEntry] match {
      case JsSuccess(newEntry, _) =>
        readScoreboard() match {
          case Success(scoreboard) =>
            // Nur aktualisieren, wenn der Score größer als der niedrigste Platz 5 ist
            if (newEntry.score > scoreboard.last.score) {
              val updatedScoreboard = updateRanking(scoreboard, newEntry)

              // Schreibe die Änderungen zurück in die Datei
              writeScoreboard(updatedScoreboard) match {
                case Success(_) =>
                  Ok(Json.obj(
                    "status" -> "success",
                    "message" -> "Scoreboard updated",
                    "mode" -> "multi"
                  ))
                case Failure(ex) =>
                  InternalServerError(Json.obj(
                    "status" -> "error",
                    "message" -> ex.getMessage
                  ))
              }
            } else {
              Ok(Json.obj(
                "status" -> "no_change",
                "message" -> "New score is not high enough to update scoreboard."
              ))
            }

          case Failure(ex) =>
            InternalServerError(Json.obj(
              "status" -> "error",
              "message" -> s"Failed to read scoreboard: ${ex.getMessage}"
            ))
        }

      case JsError(errors) =>
        BadRequest(Json.obj(
          "status" -> "error",
          "message" -> "Invalid JSON",
          "details" -> errors.toString
        ))
    }
  }

}
