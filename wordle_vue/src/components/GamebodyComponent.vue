<script setup>
import { ref, onMounted } from 'vue';
import WordInput from "@/components/WordInputComponent.vue";
import Keyboard from "@/components/KeyboardComponent.vue";
import GameBoard from "@/components/GameBoardComponent.vue";
import api from "../api/api.js";

// Define the events emitted by this component
const emit = defineEmits(['toggle']);
const gameboard = ref(null); // Reactive gameboard data

const loadGameboard = async () => {
  try {
    const response = await api.getGameboard();
    gameboard.value = response.data.data.gameboard; // Update the gameboard data
  } catch (error) {
    console.error("Error loading gameboard:", error);
  }
};

const giveup = async() => {
  try{
    await api.stopGame()
    console.log("Spiel wird aufgegeben")
    emit('toggle')
  }catch (error){
    console.error('Fehler beim Beenden des Spiels:', error);
  }
};

// Load the gameboard when the component is mounted
onMounted(() => {
  loadGameboard();
});
</script>

<template>
  <div class="gamebody">
    <WordInput @submit-word="submitWord" :loadGameboard="loadGameboard" />
    <GameBoard :gameboard="gameboard" />
    <Keyboard />
    <div>
      <v-btn @click="giveup">Aufgeben</v-btn>
    </div>
  </div>
</template>

<style scoped>
/* Add your styles here */
</style>