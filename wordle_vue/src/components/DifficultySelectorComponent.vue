<script setup>
import { defineEmits } from 'vue';

// Definiere die Events, die von dieser Komponente gesendet werden
const emit = defineEmits(['toggle', 'select-difficulty']);

// Funktion zum Auslösen des Events, wenn ein Button gedrückt wird
const handleButtonClick = (endpoint) => {
  emit('select-difficulty', endpoint);  // Sende das "select-difficulty"-Event mit dem gewählten Endpunkt
  emit('toggle');  // Sende das "toggle"-Event, um die Ansicht zu wechseln
};
</script>

<template>
  <div class="difficulty-selector">
    <h2>Wähle deine Schwierigkeitsstufe</h2>
    <div>
      <!-- Button zum Umschalten und Backend-Abfrage -->
      <v-container>
        <v-btn color="primary" @click="handleButtonClick('http://localhost:9000/new/1/solo/anonym')">Leicht</v-btn>
        <v-btn color="primary" @click="handleButtonClick('http://localhost:9000/new/2/solo/anonym')">Mittel</v-btn>
        <v-btn color="primary" @click="handleButtonClick('http://localhost:9000/new/3/solo/anonym')">Schwer</v-btn>
      </v-container>
    </div>
  </div>
</template>

<style scoped>

</style>