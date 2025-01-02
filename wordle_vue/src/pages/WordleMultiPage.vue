<script setup>
import GAMEBODY from "../components/GamebodyComponent.vue";
import LOBBYCOMPONENT from "../components/LobbyComponent.vue";
import { ref } from 'vue';
import '../assets/main.css'; // Import the common CSS


// Zustand für den Login und Benutzername
const isLoggedIn = ref(false); // Ob der Benutzer eingeloggt ist
const username = ref(""); // Benutzername
const errorMessage = ref('');
// State to decide which component to show
const isComponentAVisible = ref(false);  // true shows ComponentA, false shows ComponentB



// Funktion für den Login
const submitUsername = () => {
  // Überprüfen, ob der Benutzername leer ist
  if (!username.value.trim()) {
    errorMessage.value = 'Benutzername darf nicht leer sein.';
    return;
  }

  // Überprüfen, ob der Benutzername Sonderzeichen enthält
  const usernameRegex = /^[a-zA-Z0-9_]+$/; // Nur Buchstaben, Zahlen und Unterstrich
  if (!usernameRegex.test(username.value)) {
    errorMessage.value = 'Benutzername darf keine Sonderzeichen enthalten.';
    return;
  }

  // Wenn alles passt, anmelden
  errorMessage.value = ''; // Fehlernachricht zurücksetzen
  isLoggedIn.value = true;
};

// Function to toggle the state
const toggleComponent = () => {
  isComponentAVisible.value = !isComponentAVisible.value;
};

const handleGameOver = (message) => {
  console.log("Game over event received with message: ", message);
  isComponentAVisible.value = false;
};

</script>

<template>
  <div>
    <!-- Login-Bereich -->
    <div v-if="!isLoggedIn">
      <input
          type="text"
          v-model="username"
          placeholder="Benutzernamen eingeben"
      />
      <button @click="submitUsername">Login</button>

      <!-- Fehlernachricht anzeigen -->
      <p v-if="errorMessage" style="color: red;">{{ errorMessage }}</p>
    </div>

    <!-- Hauptinhalt -->
    <div v-else>
      <h2>Hallo, {{ username }}!</h2>
      <GAMEBODY
          v-if="isComponentAVisible"
          @game-over="handleGameOver"
          @toggle="toggleComponent"
      />
      <LOBBYCOMPONENT
          v-if="!isComponentAVisible"
          @toggle="toggleComponent"
      />
    </div>
  </div>
</template>