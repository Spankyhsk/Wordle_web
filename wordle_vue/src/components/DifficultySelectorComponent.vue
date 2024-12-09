<script setup>
import {defineEmits, ref} from 'vue';
import api from '../api/api'

// Define the events emitted by this component
const emit = defineEmits(['toggleSolo']);

// UI-Status-Flags
const isLoading = ref(false); // Ladeanzeige Flag

// Funktion, um die Schwierigkeitsstufe auszuwählen
const handleButtonClick = async (difficulty) => {
  isLoading.value = true; // Ladeanzeige aktivieren
  try {
    // API-Aufruf zum Starten des Spiels
    await api.newGame(difficulty, "solo", "anonym");  // Hier wird das API aufgerufen, um ein neues Spiel zu starten

    // Emit für das Umschalten der Ansicht
    emit('toggleSolo');  // Umschalten der Ansicht nach erfolgreichem API-Aufruf

  } catch (error) {
    console.error('Fehler beim Starten des Spiels:', error);
    // Fehlerbehandlung: Zeige eine Fehlermeldung, wenn das Spiel nicht gestartet werden konnte
  } finally {
    isLoading.value = false; // Ladeanzeige deaktivieren
  }
};
</script>

<template>
  <div class="difficulty-selector">
    <h2>Select Your Difficulty Level</h2>
    <div>
      <!-- Buttons to toggle and query the backend -->
      <v-container>
        <v-btn color="primary" @click="handleButtonClick(1)">Easy</v-btn>
        <v-btn color="primary" @click="handleButtonClick(2)">Medium</v-btn>
        <v-btn color="primary" @click="handleButtonClick(3)">Hard</v-btn>
      </v-container>
    </div>
    <!-- Optional: Ladeanzeige anzeigen, während das Spiel geladen wird -->
    <v-overlay v-if="isLoading">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
  </div>

</template>

<style scoped>
/* Add your styles here */
</style>