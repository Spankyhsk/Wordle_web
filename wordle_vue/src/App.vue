<template>
  <v-app>
    <!-- Background image -->
    <v-container class="background-Img" fluid>
      <!-- Navigation Drawer -->
      <v-navigation-drawer
          v-model="drawer"
          location="right"
          temporary
          class="custom-drawer"
      >
        <!-- Drawer Header mit identischem Drawer-Icon -->
        <v-sheet elevation="0" color="transparent" class="d-flex align-center justify-end" height="64px">
          <v-app-bar-nav-icon @click="toggleDrawer" color="white"/>
        </v-sheet>
        <v-list>
          <v-list-item>
            <v-btn
                width="100px"
                rounded="lg"
                variant="elevated"
                color="amber"
                to="/"
            >
              Regeln
            </v-btn>
          </v-list-item>
          <v-list-item>
            <v-btn
                width="100px"
                rounded="lg"
                variant="elevated"
                color="amber"
                to="/solo"
            >
              Solo
            </v-btn>
          </v-list-item>
          <v-list-item>
            <v-btn
                width="100px"
                rounded="lg"
                variant="elevated"
                color="amber"
                to="/multi"
            >
              Multi
            </v-btn>
          </v-list-item>
        </v-list>
      </v-navigation-drawer>
      <v-app-bar app color="transparent" class="custom-app-bar-background">
        <!-- Logo -->
        <img
            alt="Wordle logo"
            class="wordleLogo"
            src="./assets/logo.png"
        >
        <v-spacer></v-spacer>
        <v-app-bar-nav-icon v-if="!drawer" @click="toggleDrawer" />
      </v-app-bar>
      <!-- Hauptinhalt: Wird basierend auf den Routen geändert -->
      <v-main>
        <v-container fluid>
          <!-- Wenn der Benutzer offline ist, wird die Offline-Seite angezeigt -->
          <OfflinePage v-if="!isOnline" />
          <router-view v-else />
        </v-container>
      </v-main>
    </v-container>
    <!-- Footer -->
    <v-footer class="custom-footer" color="transparent" elevation="0">
      <v-row align="center" justify="center" class="mt-9">
        <v-col cols="12" class="text-center">
          <span>&copy; 2024 - Die Ersatzbank </span>
        </v-col>
      </v-row>
    </v-footer>
  </v-app>
</template>

<script setup>
//import 'vuetify/styles';
import { ref, onMounted, onBeforeUnmount } from 'vue';
import OfflinePage from './pages/OfflinePage.vue'; // Deine Offline-Seite importieren
import { useDisplay } from 'vuetify';

const drawer = ref(false);
const isMobile = ref(false);
const { smAndDown } = useDisplay();

const updateIsMobile = () => {
  console.log('Mobile:', smAndDown.value);
  isMobile.value = smAndDown.value;
};

const toggleDrawer = () => {
  drawer.value = !drawer.value;
};

// Überprüfe den Verbindungsstatus
const isOnline = ref(navigator.onLine);

// Funktion, um den Online-Status zu aktualisieren
const updateOnlineStatus = () => {
  isOnline.value = navigator.onLine;
};

// Wenn die Komponente gemountet wird, füge Event-Listener hinzu
onMounted(() => {
  updateIsMobile();
  drawer.value = !isMobile.value;
  window.addEventListener('resize', updateIsMobile);
  window.addEventListener('online', updateOnlineStatus);
  window.addEventListener('offline', updateOnlineStatus);
});

// Entferne Event-Listener vor dem Unmounten der Komponente
onBeforeUnmount(() => {
  window.removeEventListener('resize', updateIsMobile);
  window.removeEventListener('online', updateOnlineStatus);
  window.removeEventListener('offline', updateOnlineStatus);
});
</script>

<style>
.background-Img {
  background-image: url('./assets/images/background.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  min-height: 80vh;
}

.custom-drawer {
  background-color: rgba(0, 0, 0, 0.5) !important; /* Dark transparent background */
}

.custom-footer {
  background-color: rgba(0, 0, 0, 0.0) !important;
  background-image: url('./assets/images/messagebox_background.png') !important; /* Dein transparentes PNG */
  background-size: cover !important;
  background-position: center !important;
  background-repeat: no-repeat !important;
  height: fit-content !important;
}

.wordleLogo {
  width: 12rem !important;
  display: block;
  margin: 0 auto;
}

.v-application {
  background-color: rgba(0, 0, 0, 0.0) !important;
}

.custom-app-bar-background {
  background-image: url('./assets/images/background.png') !important;
  background-color: rgb(0, 0, 0) !important;
}

</style>
