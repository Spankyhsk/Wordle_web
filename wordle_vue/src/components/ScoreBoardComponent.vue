<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const scores = ref([]);

const fetchScoreboard = async () => {
  try {
    const response = await axios.get('/scoreboard');
    scores.value = response.data.scoreboard;
  } catch (error) {
    console.error('Fehler beim Laden des Scoreboards.');
  }
};

onMounted(() => {
  fetchScoreboard();
});
</script>

<template>
  <div id="scoreboard-Container">
    <h2>Scoreboard</h2>
    <table id="scoreboard">
      <thead>
        <tr>
          <th>Position</th>
          <th>Name</th>
          <th>Score</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="score in scores"
          :key="score.position"
        >
          <td>{{ score.position }}</td>
          <td>{{ score.name }}</td>
          <td>{{ score.score }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
/* Add your styles here */
</style>