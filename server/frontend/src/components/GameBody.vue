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

</style>