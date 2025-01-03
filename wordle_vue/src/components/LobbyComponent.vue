<script setup>
import ScoreBoardComponent from "@/components/ScoreBoardComponent.vue";
import ChatRoomComponent from "@/components/ChatRoomComponent.vue";
import api from "@/api/api.js";
import {defineEmits, defineProps} from 'vue';


const props = defineProps({
  playername: String,
});

const emit = defineEmits(['toggle']);

const handleStartGameButtonClick = async () => {
  try {
    // API-Aufruf zum Starten des Spiels
    await api.newGame(1, "multi", props.playername);  // Hier wird das API aufgerufen, um ein neues Spiel zu starten
    console.log("Game started!" + props.playername);
    // Emit für das Umschalten der Ansicht
    emit('toggle');  // Umschalten der Ansicht nach erfolgreichem API-Aufruf

  } catch (error) {
    console.error('Fehler beim Starten des Spiels:', error);
    // Fehlerbehandlung: Zeige eine Fehlermeldung, wenn das Spiel nicht gestartet werden konnte
  }
};
</script>

<template>
  <div>
    <div id="ScoreBoard">
      <ScoreBoardComponent />
    </div>

    <div id="ChatRoom">
      <ChatRoomComponent />
      <v-btn
        color="success"
        @click="handleStartGameButtonClick"
      >
        Spiel starten!
      </v-btn>
    </div>
  </div>
</template>

<style scoped>

</style>