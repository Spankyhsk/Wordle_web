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

</style>