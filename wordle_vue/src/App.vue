<template>
  <img alt="Wordle logo" class="wordleLogo" src="./assets/logo.png">
  <component :is="currentView" />
</template>

<script setup>
import {ref, computed} from "vue";
import RULEPAGE from './pages/rulePage.vue'
import NOTFOUND from './pages/NotFound.vue'
const routes = {
  '/': RULEPAGE
}

const currentPath = ref(window.location.hash)

window.addEventListener('hashchange', () => {
  currentPath.value = window.location.hash
})

const currentView = computed(() => {
  return routes[currentPath.value.slice(1) || '/'] || NOTFOUND
})
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
.wordleLogo {
  width: 35vw;
  display: block;
  margin: 0 auto;
}
</style>
