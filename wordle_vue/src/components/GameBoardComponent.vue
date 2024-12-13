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
</script>

<template>
  <div v-if="isLoading">
    <p>Loading gameboard...</p>
  </div>
  <div v-else-if="gameboard && gameboard.length > 0" id="gameboard">
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
    <p>No gameboard data available.</p>
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