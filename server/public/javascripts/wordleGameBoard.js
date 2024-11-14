function loadJsonData(){
    $.ajax({
        url: '',
        method: 'GET',
        dataType: 'json',
        success: function(data){
            let formattedOutput = '<div class="gamefield">' + data.content + '</div>';

            // Ersetze Zeilenumbrüche und ANSI-Codes mit HTML-Elementen
            formattedOutput = formattedOutput
                .replace(/\n\n/g, '</div><div class="gamefield">')
                .replace(/\n/g, '<br>')
                .replace(/\u001B\[33m/g, "<span class='orange'>")
                .replace(/\u001B\[32m/g, "<span class='green'>")
                .replace(/\u001B\[0m/g, "</span>");

            // Füge das Ergebnis in das HTML ein
            $('#gameboard').html('formattedOutput'); //Bestehenden Inhalt löschen

        },
        error: function(){
            $('#gameboard').text('Fehler beim Laden der Daten')
        }
    });
}

//JSON-Daten laden, wenn das Dokument fertig ist
$(document).ready(function(){
    loadJsonData();
});