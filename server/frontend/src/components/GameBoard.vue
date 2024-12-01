<template>
  <div id="gameboard"></div> <!-- Der Container bleibt gleich wie in deinem bestehenden Code -->
</template>

<script>
import { loadJsonData } from "/assets/javascripts/wordleGameBoard.js";

export default {
  mounted() {
    // Sobald die Komponente geladen ist, wird die bestehende Logik aus GameBoard.js aufgerufen
    loadJsonData();
  },
};
</script>

<style scoped>
/* Hier kannst du spezifische Stile für den Gameboard-Container definieren */
#gameboard {
  display: flex; /* Flexbox aktivieren */
  flex-wrap: wrap;
  justify-content: center; /* Horizontale Zentrierung der Gamefields */
  align-items: normal; /* Vertikale Zentrierung der Gamefields */
  margin-top: auto;
}

/* Note: muss man noch testen ob das geht, da .gamefield noch nicht vorhanden ist und möglicherweise nicht lädt*/
.gamefield {
  font-family: 'Wordlefont-Regular';
  font-size: 2.5em;
  line-height: normal;
  text-align: center; /* Text zentrieren */
  margin: 3vw 2vw 2.5vw 2vw;
}
</style>