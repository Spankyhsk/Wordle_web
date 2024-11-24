// Funktion zum Laden des Scoreboards
function fetchScoreboard() {
    $.ajax({
        url: "/scoreboard", // Der Endpunkt
        method: "GET",
        dataType: "json",
        success: function (data) {
            renderScoreboard(data.scoreboard); // JSON-Daten verarbeiten
        },
        error: function () {
            console.error("Fehler beim Laden des Scoreboards.");
        }
    });
}

// Funktion zum Rendern des Scoreboards im HTML
function renderScoreboard(scoreboard) {
    const tbody = $("#scoreboard tbody");
    tbody.empty(); // Inhalt leeren
    scoreboard.forEach(entry => {
        tbody.append(`
            <tr>
                <td>${entry.position}</td>
                <td>${entry.name}</td>
                <td>${entry.score}</td>
            </tr>
        `);
    });
}

$(document).ready(function () {
    fetchScoreboard();
});


