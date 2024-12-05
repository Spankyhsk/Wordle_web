# Wordle_web

Die Fortsetzung vom SE-Projekt Wordle
Dies ist die Webanwendung für das Wordle-Spiel, das mit einem Vue-Frontend und einem Play-Framework-Backend entwickelt wurde.

## Voraussetzungen

Stelle sicher, dass die folgenden Tools installiert sind:

- [Node.js und npm](https://nodejs.org/)
- [SBT](https://www.scala-sbt.org/)

## Installation

### 1. Klone das Repository:

```bash
git clone https://github.com/dein-benutzername/wordle_web.git
cd wordle_web
````
### 2. Installiere die Abhängigkeiten:
- Für das Frontend (Vue) navigiere zum Vue-Ordner und installiere die Abhängigkeiten:

````bash
cd wordle_vue
npm install
cd ..
````
- Für das Backend (Play Framework) stelle sicher, dass du SBT installiert hast und dann die Abhängigkeiten im server-Verzeichnis installierst:

````bash
cd server
sbt
# Das lädt alle Abhängigkeiten
````
## Anwendung starten
Um die Anwendung zu starten, gehe in das Root-Verzeichnis des Projekts und führe den folgenden Befehl aus:

````bash
npm run start
````
Dies wird sowohl das Frontend (Vue) als auch das Backend (Play Framework) gleichzeitig starten.

## Verfügbare Ports
- Play Framework (Backend): http://localhost:9000
- Vue Framework (Frontend): http://localhost:8080
Nach dem Start kannst du die Anwendung auf diesen URLs im Browser öffnen.

## Beenden der Anwendung
Um die Anwendung zu stoppen, kannst du einfach Strg + C in deinem Terminal drücken.

