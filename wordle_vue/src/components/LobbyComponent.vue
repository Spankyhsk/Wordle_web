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
  <v-container>
    <!-- Layout for large screens -->
    <v-row class="d-none d-sm-flex">
      <v-col cols="6">
        <div id="ScoreBoard">
          <ScoreBoardComponent />
        </div>
      </v-col>
      <v-col cols="6">
        <div id="ChatRoom">
          <ChatRoomComponent
              :chatname=playername
          />
        </div>
      </v-col>
    </v-row>

    <!-- Layout for small screens -->
    <v-row class="d-sm-none">
      <v-col cols="12">
        <div id="ScoreBoard">
          <ScoreBoardComponent />
        </div>
      </v-col>
      <v-col cols="12">
        <div id="ChatRoom">
          <ChatRoomComponent
              :chatname=playername
          />
        </div>
      </v-col>
    </v-row>

    <v-btn
      color="success"
      @click="handleStartGameButtonClick"
    >
      Spiel starten!
    </v-btn>
  </v-container>
</template>

<style scoped>

</style>