<script>
import { fetchScoreboard } from "/assets/javascripts/scoreboard.js";

export default {
  mounted() {
    // Bestehende Logik wird aufgerufen
    fetchScoreboard();
  },
};
</script>

<template>
  <div id="scoreboard-container">
    <table id="scoreboard">
      <thead>
      <tr>
        <th>Position</th>
        <th>Name</th>
        <th>Score</th>
      </tr>
      </thead>
      <tbody></tbody> <!-- Der Inhalt wird weiterhin durch die bestehende Funktion gerendert -->
    </table>
  </div>
</template>

<style scoped>
#scoreboard-container{
  grid-row-gap: 15px;
  flex-flow: column;
  justify-content: center;
  align-items: center;
  display: flex;
}

#scoreboard {
  padding: 30px;
  width: 300px;
  height: 400px;
  box-shadow: 2px 4px 8px rgba(0, 0, 0, 0.7) inset;
  border-radius: 12px;
  align-items: start; /* Vertikale Ausrichtung */
  table-layout: fixed;
}
#scoreboard thead tr th {
  padding-top: 20px;
}
</style>