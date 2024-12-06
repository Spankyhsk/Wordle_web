<script setup>
import { defineEmits } from 'vue';

// Define the events emitted by this component
const emit = defineEmits(['toggle', 'select-difficulty']);

// Function to handle button click
const handleButtonClick = (difficulty) => {
  emit('select-difficulty', `/new/${difficulty}/solo/anonym`);  // Emit the selected difficulty endpoint
  emit('toggle');  // Emit the toggle event to switch the view
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
  </div>
</template>

<style scoped>
/* Add your styles here */
</style>