<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const gameboard = ref([]);

const loadJsonData = async () => {
  try {
    const response = await axios.get('/gameboard');
    renderGameBoard(response.data);
  } catch (error) {
    console.error('Error loading game board:', error);
  }
};

const renderGameBoard = (data) => {
  gameboard.value = data.gameboard.map(board => {
    const sortedEntries = Object.entries(board.gamefield).sort(([keyA], [keyB]) => keyA - keyB);
    return {
      ...board,
      gamefield: sortedEntries.map(([key, value]) => value
        .replace(/\u001B\[32m/g, "<span style='color: green;'>")
        .replace(/\u001B\[33m/g, "<span style='color: orange;'>")
        .replace(/\u001B\[0m/g, "</span>")
      )
    };
  });
};

onMounted(() => {
  loadJsonData();
});
</script>

<template>
  <div id="gameboard">
    <div class="gamefield" v-for="(field, index) in gameboard" :key="index">
      <div v-for="(value, key) in field.gamefield" :key="key" v-html="value"></div>
    </div>
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