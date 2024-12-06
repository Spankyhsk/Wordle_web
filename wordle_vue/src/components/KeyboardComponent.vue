<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const keys = ref([]);

const loadKeyboard = async () => {
  try {
    const response = await axios.get('/keyboard');
    keys.value = response.data.keys;
  } catch (error) {
    console.error('Fehler beim Laden der Tastatur:', error);
  }
};

const handleKeyClick = (keyValue) => {
  const inputField = document.getElementById('wordInput');

  if (keyValue === '←') {
    inputField.value = inputField.value.slice(0, -1);
  } else if (keyValue === '↵') {
    submitInput();
  } else {
    inputField.value += keyValue;
  }
};

const submitInput = () => {
  const inputField = document.getElementById('wordInput');
  const submitButton = document.getElementById('wordAbsendenButton');
  const enteredText = inputField.value;

  if (enteredText) {
    submitButton.click();
    inputField.value = '';
  }
};

onMounted(() => {
  loadKeyboard();
});
</script>

<template>
  <div id="keyboard-container">
    <div class="keyboard">
      <div class="row" v-for="(row, rowIndex) in keys" :key="rowIndex">
        <div class="col" v-for="key in row" :key="key" @click="handleKeyClick(key)">
          {{ key }}
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Add your styles here */
</style>