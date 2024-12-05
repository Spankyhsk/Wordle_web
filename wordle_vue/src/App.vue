<template>

  <v-app>
    <!-- Navigationsleiste -->
    <v-app-bar app color="primary" dark>
      <v-toolbar-title><img alt="Wordle logo" class="wordleLogo" src="./assets/logo.png"></v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn text to="/">Regeln</v-btn>
      <v-btn text to="/solo">Solo</v-btn>
      <v-btn text to="/multi">Multi</v-btn>
    </v-app-bar>

    <!-- Hauptinhalt: Wird basierend auf den Routen geändert -->
    <v-main>
      <router-view></router-view>
    </v-main>

    <!-- Footer -->
    <v-footer app>
      <v-col class="text-center" cols="12">
        &copy; 2024 Die Ersatzbank
      </v-col>
    </v-footer>
  </v-app>
</template>

<script setup>
import 'vuetify/styles';
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
