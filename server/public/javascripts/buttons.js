document.getElementById('wordInput').addEventListener('input', (event) => {
    const inputField = event.target;
    const word = inputField.value;

    // Prüfen, ob die Eingabe 5 Zeichen lang ist
    if (word.length === 5 && /^[A-Za-zÄÖÜäöü]+$/.test(word)) {
        // AJAX-Request senden
        fetch('/play', {
            method: 'POST',
            body: JSON.stringify({ input: word }),
            headers: {
                'Content-Type': 'application/json',
            },
        })
            .then(response => response.json())
            .then(data => {
                // Rückmeldung anzeigen
                document.getElementById('feedback').innerText = data.message || 'Eingabe erfolgreich!';
            })
            .catch(error => {
                console.error('Error:', error);
                document.getElementById('feedback').innerText = 'Fehler beim Senden der Eingabe.';
            });
    } else {
        document.getElementById('feedback').innerText = 'Bitte genau 5 Buchstaben eingeben.';
    }
});
