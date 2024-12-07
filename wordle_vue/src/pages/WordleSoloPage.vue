<script setup>
import GAMEBODY from "../components/GamebodyComponent.vue";
import DIFFICULTYSELECTOR from "../components/DifficultySelectorComponent.vue";
import { ref } from 'vue';
import axios from 'axios';

// State to decide which component to show
const isComponentAVisible = ref(false);  // true shows ComponentA, false shows ComponentB

// State for the gameboard
const gameboard = ref(null);

// Function to toggle the state
const toggleComponent = () => {
  isComponentAVisible.value = !isComponentAVisible.value;
};
// Function to send the word
const submitWord = async (word) => {
  try {
    const response = await axios.post('/play', { input: word });
    console.log('Response from backend:', response.data);
    if (response.data.status === 'nextTurn') {
      gameboard.value = response.data.gameboard; // Update the gameboard
    } else if (response.data.status === 'gameover') {
      // Handle game over
    }
  } catch (error) {
    console.error('Error sending word:', error);
  }
};
</script>

<template>
  <div>
    <GAMEBODY v-if="isComponentAVisible" :gameboard="gameboard" @toggle="toggleComponent" @submit-word="submitWord"/>
    <DIFFICULTYSELECTOR v-if="!isComponentAVisible" @toggle="toggleComponent"/>
  </div>
</template>