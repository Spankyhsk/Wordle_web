<script setup>
import { ref, onMounted } from 'vue';
import api from "../api/api";

const gameboard = ref(null); // Daten aus der API

const loadGameboard = async () => {
  try {
    const response = await api.getGameboard();
    gameboard.value = response.data; // API-Antwort speichern
  } catch (error) {
    console.error("Error loading gameboard:", error);
  }
};

// Hilfsmethode: Escape-Sequenzen verarbeiten und HTML erzeugen
const processColor = (value) => {
  // Escape-Sequenzen in HTML umwandeln
  return value
      .replace(/\u001B\[32m/g, "<span style='color: green;'>") // Grün
      .replace(/\u001B\[33m/g, "<span style='color: orange;'>") // Orange
      .replace(/\u001B\[0m/g, "</span>") // Reset
      .replace(/\u001B\[m/g, "</span>"); // Reset für andere Farb-Reset-Sequenzen
};

// Hilfsmethode: Sortiere das Gamefield nach den Keys
const sortedGameField = (gamefield) => {
  return Object.entries(gamefield)
      .sort(([keyA], [keyB]) => {
        // Umwandlung des Schlüssels in eine Zahl, bevor der Vergleich erfolgt
        const numKeyA = parseInt(keyA, 10);  // Stelle sicher, dass es eine Zahl ist
        const numKeyB = parseInt(keyB, 10);  // Stelle sicher, dass es eine Zahl ist
        return numKeyA - numKeyB;  // Vergleiche die numerischen Werte
      })
      .map(([, value]) => value); // Gebe nur die Werte zurück
};


// Hole das Gameboard, wenn die Komponente geladen wird
onMounted(() => {
  loadGameboard();
});
</script>

<template>
  <div v-if="gameboard && gameboard.length > 0" id="gameboard">
    <div
        v-for="(board, index) in gameboard"
        :key="index"
        class="gamefield"
        :id="'gamefield-' + (index + 1)"
    >
      <div v-for="(value, key) in sortedGameField(board.gamefield)" :key="key">
        <span v-html="processColor(value)"></span>
      </div>
    </div>
  </div>
  <div v-else>
    <p>Loading gameboard...</p>
  </div>
</template>

<style scoped>
#gameboard {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.gamefield {
  display: flex;
  justify-content: center;
  margin: 10px 0;
}

.gamefield div {
  margin: 0 5px;
  padding: 10px;
  border: 1px solid #ccc;
  text-align: center;
}
</style>