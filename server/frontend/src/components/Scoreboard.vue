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

</style>