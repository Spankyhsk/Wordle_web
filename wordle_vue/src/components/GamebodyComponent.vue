<script setup>
import WordInput from "@/components/WordInputComponent.vue";
import Keyboard from "@/components/KeyboardComponent.vue";
import GameBoard from "@/components/GameBoardComponent.vue";
import api from "../api/api.js";
import {onMounted, ref} from "vue";

const gameboardRef = ref(null);

// Define the events emitted by this component
const emit = defineEmits(['game-over','toggle']);




const giveup = async() => {
  try{
    console.log("Spiel wird aufgegeben")
    emit('toggle')
    await api.stopGame()
  }catch (error){
    console.error('Fehler beim Beenden des Spiels:', error);
  }
};

const reloadGameboard = () => {
  if (gameboardRef.value) {
    gameboardRef.value.loadGameboard();
  }
};

onMounted(() => {
  reloadGameboard();
});

</script>

<template>
  <div class="gamebody">
    <WordInput
      @submit-word="submitWord"
      @reload-gameboard="reloadGameboard"
      @game-over="$emit('game-over', $event)"
    />
    <GameBoard ref="gameboardRef" />
    <Keyboard />
    <div>
      <v-btn @click="giveup">
        Aufgeben
      </v-btn>
    </div>
  </div>
</template>

<style scoped>
/* Add your styles here */
</style>