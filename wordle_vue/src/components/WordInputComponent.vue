<script setup>
import { defineEmits } from 'vue';

// Define the events emitted by this component
const emit = defineEmits(['submit-word']);

// Function to handle word submission
const submitWord = () => {
  const inputField = document.getElementById('wordInput');
  const word = inputField.value;
  if (word) {
    inputField.value = '';
    console.log('Word submitted:', word);
    emit('submit-word', word); // Emit the word to the parent component
  }
};
</script>

<template>
  <div>
    <input id="wordInput" type="text" placeholder="Enter your word" />
    <v-btn @click="submitWord">Submit</v-btn>
  </div>
</template>

<style scoped>
/* Add your styles here */
</style>