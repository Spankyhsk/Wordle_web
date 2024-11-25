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


document.addEventListener('DOMContentLoaded', function () {
    const inputField = document.querySelector('input[type="text"]');
    const submitButton = document.querySelector('#submitButton');

    inputField.addEventListener('input', function () {
        const inputValue = inputField.value.trim();
        if (inputValue) {
            // Route mit dem Eingabewert dynamisch setzen
            submitButton.href = `/new/${encodeURIComponent(inputValue)/"multi"}`;
            submitButton.classList.remove('disabled');
        } else {
            submitButton.href = '#'; // Leerer Link, falls kein Input vorhanden
            submitButton.classList.add('disabled');
        }
    });
});