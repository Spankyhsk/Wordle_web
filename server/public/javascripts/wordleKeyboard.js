//------------------------------------------------------

            //Keyboard schreiben

//------------------------------------------------------
function keyboardListener(){
    const inputField = document.getElementById('wordInput');
    const keys = document.querySelectorAll('div[data-key="key"]');

    keys.forEach(key => {
        key.addEventListener('click', () => {
            const keyValue = key.textContent;

            if (keyValue === '←') {
                // Entfernt das letzte Zeichen im Eingabefeld
                inputField.value = inputField.value.slice(0, -1);
            } else if(keyValue === '↵'){
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
    const inputField = document.getElementById('wordInput'); // Input-Feld-Referenz hinzufügen
    const submitButton = document.getElementById('wordAbsendenButton');
    const enteredText = inputField.value;

        if (enteredText) {
            submitButton.click();
            inputField.value = ''; // Eingabefeld zurücksetzen
        }
}

//--------------------------------------------------------

            //keyboard laden

//--------------------------------------------------------

function loadKeyboard(){
    fetch('/keyboard')
            .then(response => response.text())
            .then(data => {
                document.getElementById('keyboard-container').innerHTML = data;
                keyboardListener(); // Event-Listener für die Tastatur-Tasten hinzufügen
                updateKeyboardColors(keyColorMap);
            })
            .catch(error => console.error('Fehler beim Laden der Tastatur:', error));
    }


//------------------------------------------------------

//keyboard färben

//------------------------------------------------------

let keyColorMap = {}; // Global speichern, um Farben zu behalten


// In `processGameboardAndUpdateKeyboard()` dann die `keyColorMap` immer updaten:
function processGameboardAndUpdateKeyboard() {
    console.log("processGameboardAndUpdateKeyboard ausgeführt");
    const gameboard = document.getElementById('gameboard');
    if (!gameboard) return;

    const gamefields = gameboard.querySelectorAll('.gamefield');

    // Temporär halten, bevor das neue `keyColorMap` angewendet wird
    const newKeyColorMap = {};

    gamefields.forEach(gamefield => {
        const spans = gamefield.querySelectorAll('span');
        spans.forEach(span => {
            const text = span.textContent.trim().toUpperCase();
            const color = span.style.color;

            if (!text.match(/^[A-Z]$/)) return;

            if (color === 'green') {
                newKeyColorMap[text] = 'green';
            } else if (color === 'orange' && newKeyColorMap[text] !== 'green') {
                newKeyColorMap[text] = 'orange';
            }
        });
    });

    keyColorMap = newKeyColorMap; // Global speichern, um wiederzuverwenden
    updateKeyboardColors(keyColorMap);
}


// Funktion zum Umfärben der Tastatur basierend auf der keyColorMap
function updateKeyboardColors(keyColorMap) {
    console.log('updateKeyboardColors');
    const keyboardKeys = document.querySelectorAll('div[data-key="key"]'); //Tasten haben die Klasse "key"

    keyboardKeys.forEach(key => {
        const keyText = key.textContent.trim().toUpperCase();
        console.log(keyColorMap)

        if (keyColorMap[keyText] === 'green') {
            key.classList.remove('yellow'); // Entfernen gelb, falls vorhanden
            key.classList.add('green');     // Setzen die Taste auf grün
            console.log('Wird grün');
        } else if (keyColorMap[keyText] === 'orange') {
            if (!key.classList.contains('green')) { // Nur gelb färben, wenn die Taste nicht grün ist
                key.classList.add('yellow');
                console.log('wird gelb');
            }
        }
    });
}



//----------------------------------------------

            //script ausführen

//----------------------------------------------

document.addEventListener("DOMContentLoaded", function() {
    const keyboardContainer = document.getElementById('keyboard-container');
    const gameboard = document.getElementById('gameboard');

    // Überprüfen, ob der Keyboard-Container existiert, bevor die Tastatur geladen wird


     // Überprüfen, ob das gameboard existiert, bevor der MutationObserver eingerichtet wird
         if (gameboard) {
             const gameboardObserver = new MutationObserver(mutations => {
                 mutations.forEach(mutation => {
                    console.log("Änderung erkannt:", mutation);
                     processGameboardAndUpdateKeyboard(); // Aktualisiere die Tastatur bei jeder Mutation
                 });
             });

             gameboardObserver.observe(gameboard, {
                 childList: true,    // Beobachtet das Hinzufügen/Entfernen von `gamefield`-Kindern
                 subtree: true,      // Beobachtet alle untergeordneten Elemente
                 characterData: true // Beobachtet Änderungen am Textinhalt der Elemente
             });

             console.log('MutationObserver für das Gameboard eingerichtet.');
         } else {
             console.log('Gameboard ist nicht vorhanden.');
         }

     if (keyboardContainer) {
         loadKeyboard(); // Tastatur laden
     } else {
         console.log('Keyboard Container ist nicht vorhanden.');
     }
});



document.addEventListener('keydown', function(event) {
    const inputField = document.getElementById('wordInput');
    if (event.key === 'Backspace') {
        event.preventDefault(); // Standardaktion verhindern

        // Entferne den letzten Buchstaben, falls das Eingabefeld nicht leer ist
        if (inputField.value.length > 0) {
            inputField.value = inputField.value.slice(0, -1);
        }
    }
    document.getElementById('wordInput').focus();
});
