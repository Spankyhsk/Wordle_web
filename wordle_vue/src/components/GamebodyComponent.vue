<script setup>
import WordInput from "@/components/WordInputComponent.vue";
import Keyboard from "@/components/KeyboardComponent.vue";
import GameBoard from "@/components/GameBoardComponent.vue";
import api from "../api/api"
import axios from 'axios';

// Define the events emitted by this component
const emit = defineEmits(['toggle']);

const submitWord = async (word) => {
  try {
    const response = await axios.post('/play', { input: word });
    switch(response.data.status) {
      case "nextTurn":

        break;
      case "gameover":
        window.location.href = `/gameOver/${response.data.message}`;
        break;
      case "nextRound":
        window.location.href = '/round';
        break;
      default:
        console.log("Server response: ", response.data.status);
    }
  } catch (error) {
    console.error('Error sending word:', error);
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
}

</script>

<template>
  <div class="gamebody">
    <WordInput @submit-word="submitWord" />
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