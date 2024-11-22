$(document).ready(function () {
    const chatMessages = $("#chat-messages");
    const messageInput = $("#message-input");
    const sendButton = $("#send-button");

    // Nachricht hinzufügen
    function addMessage(sender, text) {
        const message = $("<p>").html(`<strong>${sender}:</strong> ${text}`);
        chatMessages.append(message);
        chatMessages.scrollTop(chatMessages[0].scrollHeight); // Automatisches Scrollen nach unten
    }

    // Nachricht senden und Antwort erhalten
    function sendMessage(userMessage) {
        $.ajax({
            url: "/chat/message", // Backend-Endpunkt (anpassen)
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify({ message: userMessage }), // Nachricht als JSON senden
            success: function (data) {
                if (data.response) {
                    addMessage("Bot", data.response); // Serverantwort anzeigen
                } else {
                    addMessage("Bot", "Keine Antwort erhalten.");
                }
            },
            error: function () {
                addMessage("System", "Es gab ein Problem mit dem Server.");
            },
        });
    }

    // Senden-Button klicken
    sendButton.on("click", function () {
        const userMessage = messageInput.val().trim();
        if (userMessage) {
            addMessage("Du", userMessage);
            messageInput.val(""); // Eingabefeld leeren
            sendMessage(userMessage); // Nachricht an den Server senden
        }
    });

    // Nachricht bei Enter senden
    messageInput.on("keydown", function (event) {
        if (event.key === "Enter") {
            sendButton.click();
        }
    });
});