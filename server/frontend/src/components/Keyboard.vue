<script>
// Importiere die bestehende Keyboard-Logik
import { loadKeyboard, processGameboardAndUpdateKeyboard } from '/assets/javascript/wordleKeyboard';

export default {
  mounted() {
    // Lade die Tastatur und setze die Observer
    loadKeyboard();

    // MutationObserver zum Überwachen des Gameboards und Aktualisieren der Tastatur
    const gameboard = document.getElementById('gameboard');
    if (gameboard) {
      const gameboardObserver = new MutationObserver(mutations => {
        mutations.forEach(() => {
          processGameboardAndUpdateKeyboard(); // Update die Tastatur
        });
      });

      gameboardObserver.observe(gameboard, {
        childList: true,
        subtree: true,
        characterData: true
      });
    }
  }
};
</script>

<template>
  <div id="keyboard-container"></div>
  <input type="text" id="wordInput" />
  <button id="wordAbsendenButton">Absenden</button>
</template>

<style scoped>
/*Note: muss man noch testen ob das geht, da Keyboard noch nicht vorhanden ist und möglicherweise nicht lädt */
.keyboard {
  display: inline-block;
  width: 35vw;
  margin-top: 5vh;
  margin-bottom: 5vh;
  align-items: center;
}

.keyboard .col{
  display: flex;
  justify-content: center;
  align-items: center;
  width: 2rem;
  height: 2rem;
  border: 1px solid #ccc; /* Rahmen um die Taste */
  border-radius: 0.4vw;   /* Runde Ecken für die Tasten */
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2); /* Schatten für 3D-Effekt */
  background-color: #f8f9fa; /* Heller Hintergrund für die Tasten */
  font-size: 1.2rem;
  cursor: pointer;
  user-select: none;
  margin: 0.15rem; /* Abstand zwischen den Tasten */
}


.keyboard .col-md-auto:hover,
.keyboard .col:hover {
  background-color: #e2e6ea; /* Hintergrundfarbe beim Hover-Effekt */
}

div[data-key="key"].yellow {
  background-color: orange;
  color: black;
}
div[data-key="key"].green {
  background-color: green;
  color: white;
}
</style>