<script setup>
import { defineEmits, defineProps, onMounted } from 'vue';
import api from "../api/api.js";

// Define the events emitted by this component
const emit = defineEmits(['submit-word']);
const props = defineProps(['loadGameboard']);

// Function to handle word submission
const submitWord = async () => {
  const inputField = document.getElementById('wordInput');
  const word = inputField.value;
  if (word) {
    inputField.value = '';
    console.log('Word submitted:', word);
    emit('submit-word', word); // Emit the word to the parent component

    try {
      const response = await api.processInput({ input: word });
      console.log("Server response:", response.data);
      switch(response.data.status) {
        case "nextTurn":
          await props.loadGameboard();
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
  }
};

// Function to focus the input field
const focusInputField = () => {
  const inputField = document.getElementById('wordInput');
  if (inputField) {
    inputField.focus();
  }
};

// Function to handle input submission
const submitInput = () => {
  const inputField = document.getElementById('wordInput');
  const submitButton = document.getElementById('wordSubmitButton');
  const enteredText = inputField.value;

  if (enteredText) {
    submitButton.click();
    inputField.value = '';
  }
};

// Add event listener to focus the input field when typing
onMounted(() => {
  document.addEventListener('keydown', (event) => {
    const inputField = document.getElementById('wordInput');
    if (event.key === 'Backspace') {
      event.preventDefault();
      const cursorPos = inputField.selectionStart;
      if (cursorPos > 0) {
        const value = inputField.value;
        inputField.value = value.slice(0, cursorPos - 1) + value.slice(cursorPos);
        inputField.setSelectionRange(cursorPos - 1, cursorPos - 1);
      }
    } else if (event.key === 'Enter') {
      event.preventDefault();
      submitInput();
    }
    focusInputField();
  });
});
</script>

<template>
  <div>
    <input id="wordInput" type="text" placeholder="Enter your word" />
    <v-btn id="wordSubmitButton" @click="submitWord">Submit</v-btn>
  </div>
</template>

<style scoped>
/* Add your styles here */
</style>