<script setup>

</script>

<template>
  <div>
    <div id="rulebody">
      <h2>Willkommen zu Wordle</h2>
      <p>
        Wordle ist ein Rätselspiel, bei dem es darum geht, verborgene Wörter zu identifizieren.<br>
        Deine Mission ist klar: Finde das versteckte Wort innerhalb einer limitierten Anzahl an Versuchen.
      </p>
      <p>Bei jedem deiner Versuche erhältst du hilfreiche Hinweise in Form von gefärbten Buchstaben:</p>
      <div>
        <p>🖤 <strong id="black">Schwarz</strong> deutet darauf hin, dass ein Buchstabe nicht im Wort enthalten ist.</p>
      </div>
      <div>
        <p>💛 <strong id="yellow">Gelb</strong> verrät dir, dass ein Buchstabe im Wort vorhanden ist, aber noch nicht am richtigen Platz.</p>
      </div>
      <div>
        <p>💚 <strong id="green">Grün</strong> bestätigt, dass ein Buchstabe korrekt ist und genau dort hingehört.</p>
      </div>

      <p>
        Möchtest du deine Rätsel-Skills weiter herausfordern? <br>
        Versuche dich an höheren Schwierigkeitsstufen, bei denen du mehrere Wörter gleichzeitig erraten musst.
      </p>

      <h4>Spielmodus Leicht</h4>
      <p>
        Das Wordle Original! Du suchst ein Wort und hast dafür sechs Versuche. <br>
        Das Lösen sollte für dich kein Problem sein, falls doch, empfehlen wir dir noch einmal die Buchstaben zu üben.
      </p>

      <h4>Spielmodus Mittel</h4>
      <p>
        Hier wird es schon interessanter. <br>
        Behältst du den Überblick und kannst zwei Wörter gleichzeitig lösen?
      </p>

      <h4>Spielmodus Schwer</h4>
      <p>
        Nur was für Wordle Maestros! <br>
        Bezwingst du den Wordle Olymp, indem du vier Wörter gleichzeitig löst? Falls ja, bist du ein ganz Großer.
      </p>
    </div>
    <v-container>
      <v-row class="button-row" justify="center">
        <v-col cols="12" sm="auto">
          <v-btn
            color="light-blue-accent-3"
            to="/solo"
          >
          Soloplayer
          </v-btn>
        </v-col>
        <v-col cols="12" sm="auto">
          <v-btn
            color="deep-purple-lighten-1"
            to="/multi"
          >
            Multiplayer
          </v-btn>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<style scoped>
#black {
  color: black;
}

#yellow {
  color: #f1c40f;
}

#green {
  color: #27ae60;
}
</style>