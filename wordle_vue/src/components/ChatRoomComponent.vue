<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';

const messages = ref([]);
const newMessage = ref("");
let ws = null;
let userId = sessionStorage.getItem('userId');

// Generate a UUID if none exists
if (!userId) {
  userId = crypto.randomUUID();
  sessionStorage.setItem('userId', userId);
}

const connectWebSocket = () => {
  if (!ws) {
    ws = new WebSocket('ws://localhost:9000/chat');

    ws.onopen = () => {
      console.log('WebSocket connected');
      const initMessage = JSON.stringify({ type: "init", userId: userId });
      ws.send(initMessage);
    };

    ws.onmessage = (event) => {
      const messageData = JSON.parse(event.data);
      const isOwnMessage = messageData.senderId === userId;
      const prefix = isOwnMessage ? '(Du)' : '';
      messages.value.push(`${prefix} ${messageData.message}`);
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

const disconnectWebSocket = () => {
  if (ws) {
    ws.close();
    ws = null;
  }
};

const sendMessage = () => {
  if (newMessage.value.trim() !== "" && ws) {
    const message = JSON.stringify({ type: "message", content: newMessage.value });
    ws.send(message);
    newMessage.value = "";
  }
};

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
        <div v-for="(message, index) in messages" :key="index">{{ message }}</div>
      </div>
    </div>

    <!-- Eingabe-Bereich -->
    <div id="input-container">
      <label for="message-input"></label>
      <input v-model="newMessage" type="text" id="message-input" placeholder="Type your message..." @keypress.enter="sendMessage" />
      <button class="btn btn-success" @click="sendMessage">Send</button>
    </div>
  </div>
</template>

<style scoped>
#chat-Container {
  border: 1px solid #ccc;
  padding: 10px;
  width: 300px;
}

#output-container {
  height: 200px;
  overflow-y: auto;
  border: 1px solid #ccc;
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
</style>