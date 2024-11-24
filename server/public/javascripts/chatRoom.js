$(document).ready(function() {
  let ws = null;

  // Funktion zur Herstellung der WebSocket-Verbindung
  function connectWebSocket() {
    if ($("#chat-Container").length > 0 && !ws) {
      console.log('WebSocket connected');
      ws = new WebSocket('ws://localhost:9000/chat');

      // Eingehende Nachrichten anzeigen
      ws.onmessage = function(event) {
        console.log('Message received:', event.data);
        const message = $('<div>').text(event.data);
        $('#message-list').append(message);

        // Scrollen, damit neue Nachrichten sichtbar sind
        $('#message-list').scrollTop($('#message-list')[0].scrollHeight);
      };

      // WebSocket-Verbindungsstatus anzeigen
      ws.onopen = function() { console.log('WebSocket connected'); };
      ws.onclose = function() { console.log('WebSocket disconnected'); };
      ws.onerror = function(error) { console.error('WebSocket error:', error); };
    }
  }

  // Funktion zum Trennen der WebSocket-Verbindung
  function disconnectWebSocket() {
    if ($("#chat-Container").length === 0 && ws) {
      console.log('WebSocket disconnected');
      ws.close();
      ws = null; // WebSocket Referenz zurücksetzen
    }
  }

  // Funktion zur Überwachung des DOM
  function observeChatContainer() {
    const chatContainer = document.getElementById('chat-Container');
    const observer = new MutationObserver(function(mutations) {
      mutations.forEach(function(mutation) {
        if (mutation.type === "childList") {
          if ($("#chat-Container").length > 0 && !ws) {
            connectWebSocket(); // WebSocket verbinden, wenn der Chat-Container hinzugefügt wird
          } else if ($("#chat-Container").length === 0 && ws) {
            disconnectWebSocket(); // WebSocket trennen, wenn der Chat-Container entfernt wird
          }
        }
      });
    });

    // Observer für das Hinzufügen/Entfernen von #chat-container im DOM einrichten
    observer.observe(document.body, {
      childList: true,  // Überwacht das Hinzufügen oder Entfernen von Kind-Elementen
      subtree: true      // Beobachtet alle Unterelemente von <body>
    });
  }

    // Starte die Überwachung des DOM
  observeChatContainer();

  // Nachricht senden
  $('#send-button').click(function() {
    const message = $('#message-input').val().trim();
    if (message && ws) {
        console.log('Sending message:', message); // Debug-Ausgabe
      ws.send(message); // Nachricht an den Server senden
      $('#message-input').val(''); // Eingabefeld leeren
    }
  });

  // Drücken der Enter-Taste zum Senden
  $('#message-input').keypress(function(event) {
    if (event.key === 'Enter') {
      $('#send-button').click();
    }
  });
});