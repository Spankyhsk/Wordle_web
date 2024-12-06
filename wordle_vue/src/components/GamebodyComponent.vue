<script setup>
import { ref, onMounted } from 'vue';
import WordInput from "@/components/WordInputComponent.vue";
import Keyboard from "@/components/KeyboardComponent.vue";
import GameBoard from "@/components/GameBoardComponent.vue";
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
      gamefield: sortedEntries.map(([, value]) => value
        .replace(/\u001B\[32m/g, "<span style='color: green;'>")
        .replace(/\u001B\[33m/g, "<span style='color: orange;'>")
        .replace(/\u001B\[0m/g, "</span>")
      )
    };
  });
};

const submitWord = async (word) => {
  try {
    const response = await axios.post('/play', { input: word });
    switch(response.data.status) {
      case "nextTurn":
        loadJsonData();
        break;
      case "gameover":
        if(response.data.mode === "multi") {
          updateScoreboard(JSON.parse(response.data.message));
        }
        window.location.href = `/gameOver/${response.data.message}`;
        break;
      case "nextRound":
        window.location.href = '/round';
        break;
      default:
        console.log("Server response: ", response.data.status);
    }
  } catch (error) {
    console.error('Error sending word:', error);
  }
};

const updateScoreboard = async (newEntry) => {
  try {
    const response = await axios.get('/scoreboard');
    const scoreboard = response.data.scoreboard;
    const updatedScoreboard = integrateNewEntry(scoreboard, newEntry);

    if (updatedScoreboard) {
      await axios.post('/scoreboard', { scoreboard: updatedScoreboard });
      alert("Scoreboard updated!");
    } else {
      alert("Scoreboard not updated. New score was too low.");
    }
  } catch (error) {
    console.error('Error updating scoreboard:', error);
  }
};

const integrateNewEntry = (scoreboard, newEntry) => {
  const { name, score } = newEntry;
  let inserted = false;

  const updatedScoreboard = scoreboard.map(entry => ({ ...entry }));

  for (let i = 0; i < updatedScoreboard.length; i++) {
    if (score > updatedScoreboard[i].score) {
      updatedScoreboard.splice(i, 0, { position: i + 1, name, score });
      inserted = true;
      break;
    }
  }

  if (inserted) {
    return updatedScoreboard
      .slice(0, 5)
      .map((entry, index) => ({ ...entry, position: index + 1 }));
  }

  return null;
};

onMounted(() => {
  loadJsonData();
});
</script>

<template>
  <div class="gamebody">
    <GameBoard :gameboard="gameboard" />
    <Keyboard />
    <WordInput @submit-word="submitWord" />
    <div>
      <v-btn @click="$emit('toggle')">Wechseln</v-btn>
    </div>
  </div>
</template>

<style scoped>
/* Add your styles here */
</style>