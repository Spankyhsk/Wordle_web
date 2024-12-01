<script>
import DifficultySelector from '../components/DifficultySelector.vue'; // Importiere die DifficultySelector-Komponente
import GameBody from '../components/GameBody.vue'; // Importiere die GameBody-Komponente

export default {
  name: 'Wordle',
  components: {
    DifficultySelector, // Füge die DifficultySelector-Komponente hinzu
    GameBody // Füge die GameBody-Komponente hinzu
  },
  data() {
    return {
      gameInProgress: false, // Steuerung, ob das Spiel läuft oder nicht
      message: 'Willkommen zum Wordle-Spiel!'
    };
  }
};
</script>

<template>
  <div class="page">
    <main>
      <!-- Wenn der Benutzer im Spiel ist -->
      <div v-if="gameInProgress">
        <GameBody />
      </div>

      <!-- Wenn das Spiel nicht läuft -->
      <div v-else>
        <DifficultySelector />
      </div>
    </main>

    <footer>
      <div class="footer-text" id="messagebox">
        <p>{{ message }}</p>
      </div>
    </footer>
  </div>
</template>

<style scoped>

</style>