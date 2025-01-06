$(document).ready(function () {
  let ws = null;
  let userId = sessionStorage.getItem('userId');

  // Generiere eine UUID, wenn keine vorhanden ist
  if (!userId) {
    userId = crypto.randomUUID();
    sessionStorage.setItem('userId', userId);
  }

  function connectWebSocket() {
    if ($("#chat-Container").length > 0 && !ws) {
      ws = new WebSocket('ws://wordle-web.onrender.com/chat');

      ws.onopen = function () {
        console.log('WebSocket connected');

        // Initiale Nachricht mit der User-ID senden
        const initMessage = JSON.stringify({
          type: "init",
          userId: userId
        });
        ws.send(initMessage);
      };

      ws.onmessage = function (event) {
        const messageData = JSON.parse(event.data);

        // Prüfen, ob die Nachricht von diesem Nutzer stammt
        const isOwnMessage = messageData.senderId === userId;
        const prefix = isOwnMessage ? '(Du)' : '';
        const messageElement = $('<div>').html(
            `<strong>${prefix}</strong> ${messageData.message}`
        );

        $('#message-list').append(messageElement);
        $('#message-list').scrollTop($('#message-list')[0].scrollHeight);
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
    if ($("#chat-Container").length === 0 && ws) {
      ws.close();
      ws = null;
    }
  }

  function observeChatContainer() {
    const observer = new MutationObserver(function (mutations) {
      mutations.forEach(function () {
        if ($("#chat-Container").length > 0 && !ws) {
          connectWebSocket();
        } else if ($("#chat-Container").length === 0 && ws) {
          disconnectWebSocket();
        }
      });
    });

    observer.observe(document.body, {
      childList: true,
      subtree: true
    });
  }

  observeChatContainer();

  $('#send-button').click(function () {
    const messageContent = $('#message-input').val().trim();
    if (messageContent && ws) {
      const message = JSON.stringify({
        type: "message",
        content: messageContent
      });
      ws.send(message);
      $('#message-input').val('');
    }
  });

  $('#message-input').keypress(function (event) {
    if (event.key === 'Enter') {
      $('#send-button').click();
    }
  });
});

//----------------------
