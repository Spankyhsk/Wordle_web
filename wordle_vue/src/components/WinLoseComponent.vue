<script setup>
import {defineProps} from 'vue';

defineProps({
  message: String
});

</script>

<template>
  <div>
    <h2>Game Over</h2>
    <p>{{ message }}</p>
  </div>
</template>

<style scoped>
p {
  margin-bottom: 20px;
}
</style>