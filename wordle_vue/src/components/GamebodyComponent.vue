<script setup>
import WordInput from "@/components/WordInputComponent.vue";
import GameBoard from "@/components/GameBoardComponent.vue";
import api from "../api/api.js";
import {onBeforeUnmount, onMounted, ref} from "vue";

const gameboardRef = ref(null);

// Define the events emitted by this component
const emit = defineEmits(['game-over','toggle']);

const gameOverTrigger = ref(false);
const toggleTrigger = ref(false);

const triggerGameOverEmit = (param) => {
  gameOverTrigger.value = true;
  emit('game-over', param);
}


const giveup = async() => {
  try{
    console.log("Spiel wird aufgegeben")
    toggleTrigger.value = true;
    emit('toggle');
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

onBeforeUnmount(async() => {
  try{
    if (!gameOverTrigger.value && !toggleTrigger.value) {
      console.log('Keiner der Emits wurde ausgelöst, Aufräumarbeit! Das Spiel wird beendet');
      // Führe hier deine Aufräumarbeiten aus
      await api.stopGame()
    } else {
      console.log('Mindestens ein Emit wurde ausgelöst, kein Cleanup erforderlich.');
    }


  }catch (error){
    console.error('Fehler beim Beenden des Spiels:', error);
  }
});
</script>

<template>
  <div class="gamebody">
    <WordInput
      @submit-word="submitWord"
      @reload-gameboard="reloadGameboard"
      @game-over="triggerGameOverEmit"
    />
    <GameBoard ref="gameboardRef" />
    <div>
      <v-btn
        color="red"
          @click="giveup">
        Aufgeben
      </v-btn>
    </div>
  </div>
</template>

<style scoped>
/* Add your styles here */
</style>