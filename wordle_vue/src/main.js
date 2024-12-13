import { createApp } from 'vue';
import App from './App.vue';

// Vuetify
import 'vuetify/styles';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';

//routes
import router from './router'; // Router importieren

// Vuetify-Instanz erstellen
const vuetify = createVuetify({
    components,
    directives,
});

// Überprüfen, ob Service Worker unterstützt wird
if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        navigator.serviceWorker
            .register('/service-worker.js')
            .then((registration) => {
                console.log('Service Worker erfolgreich registriert:', registration);
            })
            .catch((error) => {
                console.log('Service Worker Registrierung fehlgeschlagen:', error);
            });
    });
}

// Vue App initialisieren
const app = createApp(App);
app.use(router)
app.use(vuetify);
app.mount('#app');