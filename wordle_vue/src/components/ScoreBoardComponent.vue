<script setup>
import { ref, onMounted } from 'vue';
import api from "@/api/api.js"; // Deine API-Import

const scores = ref([]);

// Abrufen der Scoreboard-Daten
const fetchScoreboard = async () => {
  try {
    const response = await api.getScoreboard();
    console.log("API Response:", response);
    console.log("Scoreboard Array:", response.data.scoreboard.scoreboard);
    // Hier greifen wir auf das verschachtelte `scoreboard.scoreboard` zu
    scores.value = response.data.scoreboard.scoreboard.map(score => ({
      position: score.position,
      name: score.name || "Kein Spieler",  // Default-Wert für leere Namen
      score: score.score ?? 0  // Default-Wert für leere Scores
    }));
  } catch (error) {
    console.error('Fehler beim Laden des Scoreboards:', error);
  }
};

// Ruft die Daten beim Mounten ab
onMounted(() => {
  fetchScoreboard();
});
</script>

<template>
  <div id="scoreboard-Container">
    <h2>Scoreboard</h2>
    <!-- Zeige eine Nachricht, wenn keine Daten vorhanden sind -->
    <div v-if="scores.length === 0">Keine Daten verfügbar</div>
    <table v-else id="scoreboard">
      <thead>
      <tr>
        <th>Position</th>
        <th>Name</th>
        <th>Score</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="score in scores" :key="score.position">
        <td>{{ score.position }}</td>
        <td>{{ score.name }}</td>
        <td>{{ score.score }}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
/* Tabellenstil */
#scoreboard {
  width: 100%;
  border-collapse: collapse;
}

#scoreboard th,
#scoreboard td {
  border: 1px solid #ddd;
  padding: 8px;
}

#scoreboard th {
  background-color: #f4f4f4;
  text-align: left;
}

#scoreboard-Container {
  margin: 20px;
}
</style>
