<script>
// Importiere die bestehende Chatroom-Logik
import { connectWebSocket, disconnectWebSocket } from '/assets/javascripts/chatRoom.js';

export default {
  mounted() {
    // Wenn der Chatroom geladen wird, verbinden wir uns mit dem WebSocket
    connectWebSocket();

    // Schließe den WebSocket beim Verlassen des Chatrooms (optional)
    this.$once('hook:beforeDestroy', () => {
      disconnectWebSocket();
    });
  }
};
</script>

<template>
  <div id="chat-Container">
    <div id="message-list"></div>
    <input type="text" id="message-input" placeholder="Schreibe eine Nachricht" />
    <button id="send-button">Senden</button>
  </div>
</template>

<style scoped>

</style>