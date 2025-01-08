<script setup>
import {ref, onMounted, onBeforeUnmount, defineProps} from 'vue';
import { useRouter } from 'vue-router';

// Reactive state
const messages = ref([]);
const newMessage = ref('');
let ws = null;
let userId = sessionStorage.getItem('userId');

// Router for navigation
const router = useRouter();

const props = defineProps({
  chatname: String,
});

// Generate a UUID if none exists
if (!userId) {
  userId = crypto.randomUUID();
  sessionStorage.setItem('userId', userId);
}

// Connect to WebSocket server
const connectWebSocket = () => {
  if (!ws) {
    ws = new WebSocket('wss://wordle-web.onrender.com/chat');

    ws.onopen = () => {
      console.log('WebSocket connected');
      const initMessage = JSON.stringify({ type: 'init', userId: userId });
      ws.send(initMessage);
    };

    ws.onmessage = (event) => {
      const messageData = JSON.parse(event.data);

      // Check if the message is a timeout signal
      if (messageData.type === 'SessionTimeout') {
        handleSessionTimeout();
      } else {
        const isOwnMessage = messageData.senderId === userId;
        const prefix = isOwnMessage ? '(Du)' : '';
        messages.value.push(`${prefix} ${messageData.message}`);
      }
    };

    ws.onclose = () => {
      console.log('WebSocket disconnected');
      ws = null;
    };

    ws.onerror = (error) => {
      console.error('WebSocket error:', error);
    };
  }
};

// Disconnect WebSocket
const disconnectWebSocket = () => {
  if (ws) {
    ws.close();
    ws = null;
  }
};

// Send a message
const sendMessage = () => {
  if (newMessage.value.trim() !== '' && ws) {
    const messageContent = `${props.chatname}:\n ${newMessage.value.trim()}`
    const message = JSON.stringify({ type: 'message', content: messageContent });
    ws.send(message);
    newMessage.value = '';
  }
};

// Handle session timeout
const handleSessionTimeout = () => {
  console.warn('Session timeout received. Redirecting to login page...');
  messages.value.push('(System) Your session has timed out. Redirecting to login page...');
  setTimeout(() => {
    router.push('/multi'); // Navigate to the login page
  }, 3000);
};

// Lifecycle hooks
onMounted(() => {
  connectWebSocket();
});

onBeforeUnmount(() => {
  disconnectWebSocket();
});
</script>

<template>
  <div id="chat-Container">
    <h2>Chat Room</h2>

    <!-- Output-Bereich -->
    <div id="output-container">
      <div id="message-list">
        <div v-for="(message, index) in messages" :key="index">
          {{ message }}
        </div>
      </div>
    </div>

    <!-- Eingabe-Bereich -->
    <div id="input-container">
      <label for="message-input" />
      <input
        id="message-input"
        v-model="newMessage"
        type="text"
        placeholder="Ihre Nachricht..."
        @keypress.enter="sendMessage"
      >
      <v-btn
        color="success"
        @click="sendMessage"
      >
        Senden
      </v-btn>
    </div>
  </div>
</template>

<style scoped>
h2 {
  margin-bottom: 20px;
}
#chat-Container {
  width: 350px;
}

#output-container {
  height: 200px;
  overflow-y: auto;
  box-shadow: inset 4px 4px 8px #838386 !important;
  border-radius: 10px; /* Abgerundete Ecken */
  margin-bottom: 10px;
  padding: 5px;
}

#message-list {
  display: flex;
  flex-direction: column;
}

#input-container {
  display: flex;
  gap: 5px;
}

#message-input {
  width: 200px;
  box-shadow: 0 0 5px 2px #838386;
  border-radius: 8px;
  padding: 5px;
  margin-right: 30px;
  margin-bottom: 20px;
  font-size: 1.3em;
}

@media (max-width: 600px) {
  #message-input {
    font-size: 1em !important; /* Larger font size for mobile */
  }
}
</style>