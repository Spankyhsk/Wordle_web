<script setup>
import GAMEBODY from "../components/GamebodyComponent.vue";
import LOBBYCOMPONENT from "../components/LobbyComponent.vue";
import { ref } from 'vue';
import '../assets/main.css'; // Import the common CSS

// State to decide which component to show
const isComponentAVisible = ref(false);  // true shows ComponentA, false shows ComponentB


// Function to toggle the state
const toggleComponent = () => {
  isComponentAVisible.value = !isComponentAVisible.value;
};

</script>

<template>
  <div>
    <GAMEBODY v-if="isComponentAVisible" :gameboard="gameboard" @toggle="toggleComponent" @submit-word="submitWord"/>
    <LOBBYCOMPONENT v-if="!isComponentAVisible" @toggle="toggleComponent"/>
  </div>
</template>