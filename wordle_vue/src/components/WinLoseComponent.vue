<script setup>
import {defineProps} from 'vue';

defineProps({
  message: String
});

</script>

<template>
  <div>
    <h1>Game Over</h1>
    <p>{{ message }}</p>
  </div>
</template>

<style scoped>
/* Add your styles here */
</style>