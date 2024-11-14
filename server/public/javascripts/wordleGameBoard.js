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


//JSON-Daten laden, wenn das Dokument fertig ist
$(document).ready(function(){
    loadJsonData();
});