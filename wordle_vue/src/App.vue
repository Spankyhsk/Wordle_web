<template>
  <v-app>
    <!-- Navigationsleiste -->
    <v-app-bar
      app
      color="primary"
      dark
    >
      <v-toolbar-title>
        <img
          alt="Wordle logo"
          class="wordleLogo"
          src="./assets/logo.png"
        >
      </v-toolbar-title>
      <v-spacer />
      <v-btn
        text
        to="/"
      >
        Regeln
      </v-btn>
      <v-btn
        text
        to="/solo"
      >
        Solo
      </v-btn>
      <v-btn
        text
        to="/multi"
      >
        Multi
      </v-btn>
    </v-app-bar>

    <!-- Hauptinhalt: Wird basierend auf den Routen geändert -->
    <v-main>
      <!-- Wenn der Benutzer offline ist, wird die Offline-Seite angezeigt -->
      <OfflinePage v-if="!isOnline" />
      <router-view v-else />
    </v-main>

    <!-- Footer -->
    <v-footer app>
      <v-col
        class="text-center"
        cols="12"
      >
        &copy; 2024 Die Ersatzbank
      </v-col>
    </v-footer>
  </v-app>
</template>

<script setup>
//import 'vuetify/styles';
import { ref, onMounted, onBeforeUnmount } from 'vue';
import OfflinePage from './pages/OfflinePage.vue'; // Deine Offline-Seite importieren

// Überprüfe den Verbindungsstatus
const isOnline = ref(navigator.onLine);

// Funktion, um den Online-Status zu aktualisieren
const updateOnlineStatus = () => {
  isOnline.value = navigator.onLine;
};

// Wenn die Komponente gemountet wird, füge Event-Listener hinzu
onMounted(() => {
  window.addEventListener('online', updateOnlineStatus);
  window.addEventListener('offline', updateOnlineStatus);
});

// Entferne Event-Listener vor dem Unmounten der Komponente
onBeforeUnmount(() => {
  window.removeEventListener('online', updateOnlineStatus);
  window.removeEventListener('offline', updateOnlineStatus);
});
</script>

<style>

.wordleLogo {
  width: 35vw;
  display: block;
  margin: 0 auto;
}
</style>
