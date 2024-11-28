<script>
// Importiere die Vue-Router-Konfiguration
import { createRouter, createWebHistory } from 'vue-router';
import Wordle from './views/Wordle.vue';
import WordleMulti from './views/WordleMulti.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/solo', component: Wordle },
    { path: '/multi', component: WordleMulti }
  ]
});

export default {
  name: 'App',
  router
};
</script>

<template>
  <div id="app">
    <h1>Willkommen bei Wordle!</h1>
    <router-view />
  </div>
</template>

<style scoped>

</style>