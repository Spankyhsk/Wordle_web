import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
    plugins: [vue()],
    root: './frontend',
    build: {
        outDir: '../public/dist', // Gibt den Ausgabeordner an, in dem die gebauten Dateien abgelegt werden
        assetsDir: 'assets',
        rollupOptions: {
            input: './index.html',  // Der Einstiegspunkt für den Build
            external: [
                '/assets/javascripts/scoreboard.js', // Füge das als externes Modul hinzu
                '/assets/javascripts/chatRoom.js',
                '/assets/javascripts/wordleGameBoard.js',
                '/assets/javascript/wordleKeyboard',
            ],
        },
        emptyOutDir: true, // Löscht den Zielordner vor jedem Buil
    },
    base: '/dist/', // oder ganz entfernen, wenn nicht benötigt
    server: {
        proxy: {
            '/': 'http://localhost:9000', // Proxy für API-Aufrufe
        },
    },
})