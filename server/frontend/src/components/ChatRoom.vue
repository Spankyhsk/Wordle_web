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
#chat-Container {
  grid-row-gap: 15px;
  flex-flow: column;
  justify-content: center;
  align-items: center;
  display: flex;

}

#message-list{
  hyphens: auto;
  overflow-wrap: break-word;
  overflow-y: auto;
  padding: 20px;
  align-items: flex-end;
  text-align: right;
  width: 300px;
  height: 400px;
  box-shadow: 2px 4px 8px rgba(0, 0, 0, 0.7) inset;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
}

/* Abgerundete Ecken für das Input-Feld */
#message-input {
  width: 100%; /* Maximale Breite */
  padding: 7px; /* Innenabstand */
  font-size: 16px; /* Schriftgröße */
  border: 2px solid transparent; /* Graue Umrandung */
  border-radius: 12px; /* Abgerundete Ecken (du kannst den Wert ändern) */
  outline: none; /* Entfernt den Fokus-Rand */
  box-sizing: border-box; /* Bezieht das Padding in die Breite mit ein */
  transition: all 0.3s ease; /* Sanfte Übergänge */
  box-shadow: 1px 2px 4px rgba(0, 0, 0, 0.7) inset;
  background: none;
}

/* Hover-Effekt für das Input-Feld */
#message-input:hover {
  border-color: #aaa; /* Dunklere Umrandung bei Hover */
}

/* Fokus-Effekt für das Input-Feld */
#message-input:focus {
  border-color: #4CAF50; /* Grüner Rand, wenn das Feld fokussiert ist */
  box-shadow: 0 0 8px rgba(76, 175, 80, 0.7), inset 1px 2px 4px rgba(0, 0, 0, 0.7); /* Grüner Schatten beim Fokussieren */
}

/* Platzhalter-Stil */
#message-input::placeholder {
  color: #888; /* Hellgrauer Platzhalter */
}
</style>