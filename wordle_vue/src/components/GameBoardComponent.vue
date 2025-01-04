<script setup>
import { ref, onMounted, defineExpose } from 'vue';
import api from "../api/api.js";

const gameboard = ref(null); // Reactive reference for gameboard data
const isLoading = ref(true); // Loading state

const loadGameboard = async () => {
  try {
    const response = await api.getGameboard();
    gameboard.value = response.data.data.gameboard; // Update the reactive reference
    console.log("Gameboard value:", gameboard.value);
  } catch (error) {
    console.error("Error loading gameboard:", error);
  } finally {
    isLoading.value = false; // End loading state
  }
};

defineExpose({ loadGameboard });

// Process escape sequences and generate HTML
const processColor = (value) => {
  return value
      .replace(/\u001B\[32m/g, "<span style='color: green;'>")
      .replace(/\u001B\[33m/g, "<span style='color: orange;'>")
      .replace(/\u001B\[0m/g, "</span>")
      .replace(/\u001B\[m/g, "</span>");
};

// Sort the gamefield by keys
const sortedGameField = (gamefield) => {
  return Object.entries(gamefield)
      .sort(([keyA], [keyB]) => parseInt(keyA, 10) - parseInt(keyB, 10))
      .map(([, value]) => value);
};

// Load the gameboard when the component is mounted
onMounted(() => {
  loadGameboard();
});

const getColsDesktop = (length) => {
  if (length === 1) return 12;
  if (length === 2) return 6;
  return 3;
};

const getColsMobile = (length) => {
  if (length === 1) return 12;

  return 6;
};
</script>

<template>
  <v-container>
    <div v-if="isLoading">
      <p>Loading gameboard...</p>
    </div>
    <div
        v-else-if="gameboard && gameboard.length > 0"
        id="gameboard"
    >
      <!-- Layout für kleine Displays -->
      <v-row justify="center" class="d-sm-none">
        <v-col
            v-for="(board, index) in gameboard"
            :id="'gamefield-' + (index + 1)"
            :key="index"
            :cols="getColsMobile(gameboard.length)"
        >
          <v-card class="gamefield" elevation="outlined"  color="transparent">
            <v-card-text>
              <div
                  v-for="(value, key) in sortedGameField(board.gamefield)"
                  :key="key"
              >
                <span v-html="processColor(value)" />
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- Layout für große Displays -->
      <v-row justify="center" class="d-none d-sm-flex">
        <v-col
            v-for="(board, index) in gameboard"
            :id="'gamefield-' + (index + 1)"
            :key="index"
            :cols="getColsDesktop(gameboard.length)"
        >
          <v-card class="gamefield elevated-inset" elevation="7"  color="transparent">
            <v-card-text>
              <div
                v-for="(value, key) in sortedGameField(board.gamefield)"
                :key="key"
              >
                <span v-html="processColor(value)" />
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </div>
    <div v-else>
      <p>No gameboard data available.</p>
    </div>
  </v-container>
</template>

<style scoped>
@font-face {
  font-family: 'Wordlefont-Regular'; /*Schriftart für die geratene Wörter */
  src: url('../assets/fonts/Wordlefont-Regular.ttf') format('truetype');
}

.gamefield {
  font-family: 'Wordlefont-Regular';
  font-size: 100px !important;
}

.v-card-text {
  font-size: 2.5vw !important;
  padding: 10px 20px !important;
}
.v-card {
  padding: 0 !important;
}

.elevated-inset {
  box-shadow: inset 4px 4px 8px #838386 !important;
  border-radius: 12px!important; /* Runde Ecken */
}
/* Media query for mobile devices */
@media (max-width: 600px) {
  .v-card-text {
    font-size: 5vw !important; /* Larger font size for mobile */
  }
}
</style>