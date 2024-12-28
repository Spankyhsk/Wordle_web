<script setup>
import GAMEBODY from "../components/GamebodyComponent.vue";
import DIFFICULTYSELECTOR from "../components/DifficultySelectorComponent.vue";
import WINLOSE from "../components/WinLoseComponent.vue";
import { ref } from 'vue';
import '../assets/main.css'; // Import the common CSS

// State to decide which component to show
const isComponentAVisible = ref(false);
const winLose = ref(false);
const gameOverMessage = ref('');

// Function to toggle the state
const toggleComponent = () => {
  isComponentAVisible.value = !isComponentAVisible.value;
  winLose.value = false;
};

const handleGameOver = (message) => {
  console.log("Game over event received with message: ", message);
  winLose.value = true;
  isComponentAVisible.value = false;
  gameOverMessage.value = message;
};

</script>

<template>
  <div>
    <WINLOSE
      v-if="winLose"
      :message="gameOverMessage"
    />
    <GAMEBODY
      v-if="isComponentAVisible"
      @game-over="handleGameOver"
    />
    <DIFFICULTYSELECTOR
      v-if="!isComponentAVisible"
      @toggleSolo="toggleComponent"
    />
  </div>
</template>