<script setup>
import GAMEBODY from "../components/GamebodyComponent.vue";
import LOBBYCOMPONENT from "../components/LobbyComponent.vue";
import { ref } from 'vue';
import '../assets/main.css'; // Import the common CSS
import api from "@/api/api.js"; // Deine API-Import


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

const handleGameOver = async (message) => {
  try {
    console.log("Game over event received with message: ", message);

    // Parsen des JSON-Strings (Achte darauf, dass es gültiges JSON ist)
    const newScore = JSON.parse(message);

    // Stellen sicher, dass die notwendigen Felder vorhanden sind
    if (!newScore.name || typeof newScore.score !== 'number') {
      throw new Error('Invalid input data');
    }

    // Sende die Daten an den Server
    const response = await api.updateScoreboard(newScore);

    console.log('Scoreboard successfully updated:', response.data);
    isComponentAVisible.value = false;
  } catch (error) {
    console.error("Fehler beim Aktualisieren des Scoreboards:", error);
    // Hier kannst du die Fehlerbehandlung durchführen, falls etwas schief geht
  }
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
          :playername=username
      />
    </div>
  </div>
</template>