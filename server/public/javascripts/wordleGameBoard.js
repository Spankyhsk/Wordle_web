function loadJsonData(){
    $.ajax({
        url: '/gameboard',
        method: 'GET',
        dataType: 'json',
        success: function(data){
            renderGameBoard(data);
        },
        error: function(){
            console.error("Fehler beim Laden der Daten");
        }
    });
}

function renderGameBoard(data){
    const container = $('#gameboard'); // HTML-Container, in dem die gamefields angezeigt werden
    container.empty(); // Vorherigen Inhalt leeren

    // Durch jedes gameboard-Objekt im Array iterieren
    data.gameboard.forEach((board, index) => {
        // Erstelle einen Container für das gesamte gamefield
        const gameFieldDiv = $('<div>').addClass('gamefield').attr('id', `gamefield-${index + 1}`);

        // Sortiere die Einträge von gamefield nach Schlüssel
        const sortedEntries = Object.entries(board.gamefield).sort(([keyA], [keyB]) => keyA - keyB);

        // Geordnete Einträge durchlaufen und HTML generieren
        sortedEntries.forEach(([key, value]) => {
            // Escape-Sequenzen für Farben verarbeiten
            const coloredValue = value
                .replace(/\u001B\[32m/g, "<span style='color: green;'>") // Grün
                .replace(/\u001B\[33m/g, "<span style='color: orange;'>") // Orange
                .replace(/\u001B\[0m/g, "</span>"); // Reset

            // HTML-Element für den einzelnen Eintrag erstellen und zum gamefield-Container hinzufügen
            gameFieldDiv.append(`<div>${coloredValue}</div>`);
        });

        // Füge den gamefield-Container zum Hauptcontainer hinzu
        container.append(gameFieldDiv);
    });
}

function getWinning() {
    $.ajax({
        url: '/play', // Pfad zur anderen HTML-Seite
        type: 'GET', // GET-Methode
        dataType: 'html.scala', // Wir erwarten HTML als Antwort
        success: function(response) {
            console.log('html erfolgreich geladen')
        },
        error: function(xhr, status, error) {
            // Fehlerbehandlung
            console.error('Fehler beim Laden der HTML-Seite:', error);
        }
    });
}

function updateScoreboard(newEntry) {
    const endpoint = "/scoreboard";

    // Schritt 1: Aktuelles Scoreboard laden
    $.ajax({
        url: endpoint,
        method: "GET",
        dataType: "json",
        success: function (data) {
            const scoreboard = data.scoreboard; // Aktuelles Scoreboard
            const updatedScoreboard = integrateNewEntry(scoreboard, newEntry); // Scoreboard aktualisieren

            if (updatedScoreboard) {
                // Schritt 3: Scoreboard senden, wenn es aktualisiert wurde
                $.ajax({
                    url: endpoint,
                    method: "POST",
                    contentType: "application/json",
                    data: JSON.stringify({ scoreboard: updatedScoreboard }),
                    success: function (response) {
                        alert("Scoreboard wurde aktualisiert!");
                    },
                    error: function () {
                        alert("Fehler beim Aktualisieren des Scoreboards.");
                    }
                });
            } else {
                alert("Scoreboard wurde nicht aktualisiert. Neuer Score war zu niedrig.");
            }
        },
        error: function () {
            alert("Fehler beim Laden des Scoreboards.");
        }
    });
}

// Hilfsfunktion: Neuen Eintrag ins Scoreboard integrieren
function integrateNewEntry(scoreboard, newEntry) {
    const { name, score } = newEntry;
    let inserted = false;

    // Schritt 2: Überprüfen und aktualisieren des Scoreboards
    const updatedScoreboard = scoreboard.map(entry => ({ ...entry })); // Kopie erstellen

    for (let i = 0; i < updatedScoreboard.length; i++) {
        if (score > updatedScoreboard[i].score) {
            // Neuen Eintrag an Position `i` einfügen, Rest verschieben
            updatedScoreboard.splice(i, 0, { position: i + 1, name, score });
            inserted = true;
            break;
        }
    }

    if (inserted) {
        // Aktualisiere Positionen und schneide bei 5 Einträgen ab
        return updatedScoreboard
            .slice(0, 5)
            .map((entry, index) => ({ ...entry, position: index + 1 }));
    }

    return null; // Kein Update nötig
}

//JSON-Daten laden, wenn das Dokument fertig ist
$(document).ready(function(){
    loadJsonData();
});

//JSON-Daten laden, bei Benutzereingabe
$(document).on('submit', '.wordleWordEingabe', function(event) {
    console.log("Formular abgeschickt");  // Logge, ob der Submit-Event ausgelöst wird
    event.preventDefault(); // Verhindert das Standard-Formularverhalten


    const dataToSend = {
        input: $('#wordInput').val() // Inputfeld
    };
    console.log(dataToSend);

    $.ajax({
        url: '/play',
        type: 'POST',
        contentType: 'application/json', // Gib an, dass es sich um JSON handelt
        accept: "application/json",
        data: JSON.stringify(dataToSend),

        success: function(response) {
            switch(response.status){
                case "nextTurn":
                    loadJsonData();
                    break;
                case "gameover":
                    if(response.mode === "multi"){
                        updateScoreboard(JSON.parse(response.message))
                    }
                    window.location.href ='/gameOver/${response.message}'
                    break;
                case "nextRound":
                    window.location.href ='/round'
                    break;
                default:
                    console.log("Serverantwort: ", response.status);
            }
        },
        error: function(xhr, status, error) {
            console.error("Fehler beim Senden der Daten:", error);
            console.error("Serverantwort:", xhr.responseText); // Serverantwort bei Fehler
        }
    });
});