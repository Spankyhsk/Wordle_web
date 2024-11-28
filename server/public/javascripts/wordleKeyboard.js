let keyColorMap = {}; // Global speichern, um Farben zu behalten

function keyboardListener() {
    const inputField = document.getElementById('wordInput');
    const keys = document.querySelectorAll('div[data-key="key"]');

    keys.forEach(key => {
        key.addEventListener('click', () => {
            const keyValue = key.textContent;

            if (keyValue === '←') {
                inputField.value = inputField.value.slice(0, -1);
            } else if (keyValue === '↵') {
                submitInput();
            } else {
                inputField.value += keyValue;
            }
        });
    });
}

function submitInput() {
    const inputField = document.getElementById('wordInput');
    const submitButton = document.getElementById('wordAbsendenButton');
    const enteredText = inputField.value;

    if (enteredText) {
        submitButton.click();
        inputField.value = ''; // Eingabefeld zurücksetzen
    }
}

function loadKeyboard() {
    fetch('/keyboard')
        .then(response => response.text())
        .then(data => {
            document.getElementById('keyboard-container').innerHTML = data;
            keyboardListener(); // Event-Listener für die Tastatur-Tasten hinzufügen
            updateKeyboardColors(keyColorMap);
        })
        .catch(error => console.error('Fehler beim Laden der Tastatur:', error));
}

function updateKeyboardColors(keyColorMap) {
    const keyboardKeys = document.querySelectorAll('div[data-key="key"]');

    keyboardKeys.forEach(key => {
        const keyText = key.textContent.trim().toUpperCase();

        if (keyColorMap[keyText] === 'green') {
            key.classList.remove('yellow');
            key.classList.add('green');
        } else if (keyColorMap[keyText] === 'orange') {
            if (!key.classList.contains('green')) {
                key.classList.add('yellow');
            }
        }
    });
}

function processGameboardAndUpdateKeyboard() {
    const gameboard = document.getElementById('gameboard');
    if (!gameboard) return;

    const gamefields = gameboard.querySelectorAll('.gamefield');
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

    keyColorMap = newKeyColorMap;
    updateKeyboardColors(keyColorMap);
}

export { loadKeyboard, processGameboardAndUpdateKeyboard };
