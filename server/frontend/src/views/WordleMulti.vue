<script>
import GameBody from '../components/GameBody.vue'; // Importiere die GameBody-Komponente
import Scoreboard from '../components/Scoreboard.vue';
import Chatroom from '../components/Chatroom.vue';

export default {
  name: 'WordleMulti',
  components: {
    GameBody, // Füge die GameBody-Komponente hinzu
    Scoreboard,
    Chatroom
  },
  data() {
    return {
      gameInProgress: false
    };
  }
};
</script>

<template>
  <div class="page">
    <main>
      <!-- Wenn der Benutzer im Spiel ist -->
      <div v-if="gameInProgress">
        <GameBody />
      </div>

      <!-- Wenn das Spiel nicht läuft -->
      <div v-else>
        <Scoreboard />
        <Chatroom />
      </div>
    </main>
  </div>
</template>

<style scoped>

</style>