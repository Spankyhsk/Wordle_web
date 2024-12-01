<script>
import GameBoard from "./GameBoard.vue";
import Keyboard from "./Keyboard.vue";
export default {
  name: 'GameBody',
  components: {
    GameBoard,  // Registriere GameBoard-Komponente
    Keyboard    // Registriere Keyboard-Komponente
  }
};
</script>

<template>
  <div class="gamebody">
    <!-- Wort-Eingabeformular -->
    <form class="wordleWordEingabe">
      <label for="wordInput"></label>
      <div class="image-container-wordInput">
        <input
            type="text"
            id="wordInput"
            name="input"
            maxlength="5"
            pattern="[A-Za-zÄÖÜäöü]{5}"
            title="Bitte genau 5 Buchstaben eingeben"
            required
            autofocus
        />
      </div>
      <input
          class="btn btn-success"
          type="submit"
          value="Absenden"
          id="wordAbsendenButton"
      />
    </form>

    <!-- Gameboard -->
    <GameBoard />
    <!-- Tastatur-Container -->
    <Keyboard />

    <!-- Aufgeben-Button -->
    <div>
      <form action="/stop" method="GET">
        <button class="btn btn-danger" id="aufgeben" type="submit">Aufgeben</button>
      </form>
    </div>
  </div>
</template>

<style scoped>
/*Note: Muss noch angeguckt werden, sobald aufrufbar*/
.wordleWordEingabe {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 2vh;
}


.image-container-wordInput {
  position: relative;
  width: 20vw;
  height: 15vh;
  background-image: url('/assets/images/eingabe_background.png');
  background-size: contain; /* Bild vollständig im Container anzeigen */
  background-repeat: no-repeat;
  background-position: center; /* Bild zentrieren */
}

.image-container-wordInput input[type="text"] {
  position: absolute;
  top: 50%; /* Vertikal zentrieren (50% von oben) */
  left: 50%; /* Horizontal zentrieren (50% von links) */
  transform: translate(-50%, -50%); /* Korrektur, um das Input-Feld genau in der Mitte zu platzieren */
  width: 10vw; /* Breite des Input-Feldes */
  font-size: 1em; /* Schriftgröße im Input-Feld */
  background-color: rgb(226 228 230);
}

</style>