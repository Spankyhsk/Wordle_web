<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const keys = ref(["qwertzuiopü", "asdfghjklöä", "←yxcvbnm↵"]);

const loadKeyboard = async () => {
  try {
    const response = await axios.get('/keyboard');
    keys.value = response.data.keys;
  } catch (error) {
    console.error('Error loading keyboard:', error);
  }
};

const handleKeyClick = (keyValue) => {
  const inputField = document.getElementById('wordInput');
  const start = inputField.selectionStart;
  const end = inputField.selectionEnd;

  if (keyValue === '←') {
    if (start > 0) {
      inputField.value = inputField.value.slice(0, start - 1) + inputField.value.slice(end);
      inputField.setSelectionRange(start - 1, start - 1);
    }
  } else if (keyValue === '↵') {
    submitInput();
  } else {
    inputField.value = inputField.value.slice(0, start) + keyValue + inputField.value.slice(end);
    inputField.setSelectionRange(start + 1, start + 1);
  }
};

const submitInput = () => {
  const inputField = document.getElementById('wordInput');
  const submitButton = document.getElementById('wordSubmitButton');
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
      <div
        v-for="(row, rowIndex) in keys"
        :key="rowIndex"
        class="row"
      >
        <v-btn
          v-for="key in row"
          :key="key"
          class="col"
          @click="handleKeyClick(key)"
        >
          {{ key }}
        </v-btn>
      </div>
    </div>
  </div>
</template>

<style scoped>
#keyboard-container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.keyboard {
  display: flex;
  flex-direction: column;
}

.row {
  display: flex;
  justify-content: center;
  margin: 5px 0;
}

.col {
  margin: 0 5px;
  padding: 10px;
  border: 1px solid #ccc;
  text-align: center;
  cursor: pointer;
}
</style>