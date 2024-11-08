//------------------------------------------------------

            //Keyboard schreiben

//------------------------------------------------------
function keyboardListener(){
    const inputField = document.getElementById('keyboard-input');
    const keys = document.querySelectorAll('.key');

    keys.forEach(key => {
        key.addEventListener('click', () => {
            const keyValue = key.textContent;

            if (keyValue === 'Löschen') {
                // Entfernt das letzte Zeichen im Eingabefeld
                inputField.value = inputField.value.slice(0, -1);
            } else if(keyValue === 'Enter'){
                //Hier Eingabe bestätigen mit Enter
                submitInput();
            } else {
                // Fügt das Zeichen zum Eingabefeld hinzu
                inputField.value += keyValue;
            }
        });
    });
}

function submitInput(){
    const inputField = document.getElementById('keyboard-input'); // Input-Feld-Referenz hinzufügen
    const enteredText = inputField.value;
        if (enteredText) {
            inputField.value = ''; // Eingabefeld zurücksetzen
        }
}

//--------------------------------------------------------

            //keyboard laden

//--------------------------------------------------------

function loadKeyboard(){
    fetch('keyboard.html')
            .then(response => response.text())
            .then(data => {
                document.getElementById('keyboard-placeholder').innerHTML = data;
                keyboardListener(); // Event-Listener für die Tastatur-Tasten hinzufügen
            })
            .catch(error => console.error('Fehler beim Laden der Tastatur:', error));
    }
}

//------------------------------------------------------

//keyboard färben

//------------------------------------------------------

// Funktion zum Verarbeiten des gameboard-Inhalts und zum Aktualisieren der Tastatur
function processGameboardAndUpdateKeyboard() {
    const gameboard = document.getElementById('gameboard');
    if (!gameboard) return;

    const gamefields = gameboard.querySelectorAll('.gamefield');

    // Eine Map, um den höchsten Farbstatus für jeden Buchstaben zu verfolgen
    const keyColorMap = {};

    // Durchlaufe alle gamefield-Elemente und analysiere die Buchstaben und Farben
    gamefields.forEach(gamefield => {
        const spans = gamefield.querySelectorAll('span');
        spans.forEach(span => {
            const text = span.textContent.trim().toUpperCase();
            const color = span.style.color;

            if (!text.match(/^[A-Z]$/)) return; // Nur Buchstaben berücksichtigen

            // Priorität der Farben festlegen: grün > gelb > keine Farbe
            if (color === 'green') {
                keyColorMap[text] = 'green';
            } else if (color === 'orange' && keyColorMap[text] !== 'green') {
                keyColorMap[text] = 'orange';
            } else if (!color && !keyColorMap[text]) {
                keyColorMap[text] = 'orange';
            }
        });
    });

    // Tastaturfarben aktualisieren basierend auf den gefundenen Farben
    updateKeyboardColors(keyColorMap);
}

// Funktion zum Umfärben der Tastatur basierend auf der keyColorMap
function updateKeyboardColors(keyColorMap) {
    const keyboardKeys = document.querySelectorAll('.key'); //Tasten haben die Klasse "key"

    keyboardKeys.forEach(key => {
        const keyText = key.textContent.trim().toUpperCase();

        if (keyColorMap[keyText] === 'green') {
            key.classList.remove('yellow'); // Entfernen gelb, falls vorhanden
            key.classList.add('green');     // Setzen die Taste auf grün
        } else if (keyColorMap[keyText] === 'orange') {
            if (!key.classList.contains('green')) { // Nur gelb färben, wenn die Taste nicht grün ist
                key.classList.add('yellow');
            }
        }
    });
}

// MutationObserver einrichten, um Änderungen im gameboard zu erkennen
const gameboardObserver = new MutationObserver(mutations => {
    mutations.forEach(mutation => {
        processGameboardAndUpdateKeyboard(); // Jedes Mal, wenn sich etwas ändert, die Tastatur aktualisieren
    });
});

const gameboard = document.getElementById('gameboard');
if (gameboard) {
    gameboardObserver.observe(gameboard, {
        childList: true,    // Beobachtet das Hinzufügen/Entfernen von `gamefield`-Kindern
        subtree: true,      // Beobachtet alle untergeordneten Elemente
        characterData: true // Beobachtet Änderungen am Textinhalt der Elemente
    });
}


