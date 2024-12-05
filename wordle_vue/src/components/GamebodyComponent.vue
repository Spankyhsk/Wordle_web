<script setup>
import Keyboard  from "@/components/KeyboardComponent.vue";
import GameBoard from "@/components/GameBoardComponent.vue";
</script>

<template>
<div class="gamebody">
  <GameBoard />
  <Keyboard />
  <div>
    <!-- Button zum Umschalten, der ein Event an die Elternkomponente sendet -->
    <v-btn @click="$emit('toggle')">Wechseln</v-btn>
  </div>
</div>
</template>

<style scoped>

</style>