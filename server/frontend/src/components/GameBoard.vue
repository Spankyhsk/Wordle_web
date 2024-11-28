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
  display: flex;
  flex-wrap: wrap;
}
</style>