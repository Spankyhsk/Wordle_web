<script setup>
import GAMEBODY from "../components/GamebodyComponent.vue"
import DIFFICULTYSELECTOR from "../components/DifficultySelectorComponent.vue"
import { ref } from 'vue';
import axios from 'axios';

// Zustand, der entscheidet, welche Komponente angezeigt wird
const isComponentAVisible = ref(false);  // true zeigt ComponentA, false zeigt ComponentB

// Funktion zum Umschalten des Zustands
const toggleComponent = () => {
  isComponentAVisible.value = !isComponentAVisible.value;
};

// Funktion für den API-Aufruf
const callApi = async (endpoint) => {
  try {
    const response = await axios.get(endpoint);
    console.log('Antwort von Backend:', response.data);
    toggleComponent();  // Wechsel zu GAMEBODY nach API-Aufruf
  } catch (error) {
    console.error('Fehler beim Abrufen der Daten:', error);
  }
};
</script>

<template>
  <GAMEBODY v-if="isComponentAVisible" @toggle="toggleComponent"/>
  <DIFFICULTYSELECTOR v-if="!isComponentAVisible" @toggle="toggleComponent" @select-difficulty="callApi"/>
</template>

<style scoped>

</style>