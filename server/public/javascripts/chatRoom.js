let ws = null;
let userId = sessionStorage.getItem('userId');

// Generiere eine UUID, wenn keine vorhanden ist
if (!userId) {
  userId = crypto.randomUUID();
  sessionStorage.setItem('userId', userId);
}

function connectWebSocket() {
  if (!ws) {
    ws = new WebSocket('ws://localhost:9000/chat');

    ws.onopen = function () {
      console.log('WebSocket connected');
      const initMessage = JSON.stringify({
        type: "init",
        userId: userId
      });
      ws.send(initMessage);
    };

    ws.onmessage = function (event) {
      const messageData = JSON.parse(event.data);
      const isOwnMessage = messageData.senderId === userId;
      const prefix = isOwnMessage ? '(Du)' : '';
      const messageElement = document.createElement('div');
      messageElement.innerHTML = `<strong>${prefix}</strong> ${messageData.message}`;
      document.getElementById('message-list').appendChild(messageElement);
      document.getElementById('message-list').scrollTop = document.getElementById('message-list').scrollHeight;
    };

    ws.onclose = function () {
      console.log('WebSocket disconnected');
      ws = null;
    };

    ws.onerror = function (error) {
      console.error('WebSocket error:', error);
    };
  }
}

function disconnectWebSocket() {
  if (ws) {
    ws.close();
    ws = null;
  }
}

document.getElementById('send-button')?.addEventListener('click', function () {
  const messageContent = document.getElementById('message-input').value.trim();
  if (messageContent && ws) {
    const message = JSON.stringify({
      type: "message",
      content: messageContent
    });
    ws.send(message);
    document.getElementById('message-input').value = '';
  }
});

document.getElementById('message-input')?.addEventListener('keypress', function (event) {
  if (event.key === 'Enter') {
    document.getElementById('send-button').click();
  }
});

export { connectWebSocket, disconnectWebSocket };
