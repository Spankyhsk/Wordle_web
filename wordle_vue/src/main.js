import { createApp } from 'vue';
import App from './App.vue';

// Vuetify
import 'vuetify/util/colors'
import 'vuetify/styles';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import '@mdi/font/css/materialdesignicons.css'; // Ensure you are using css-loader

//routes
import router from './router'; // Router importieren

import './registerServiceWorker';

// Vuetify-Instanz erstellen
const vuetify = createVuetify({
    components,
    directives,
    icons: {
        defaultSet: 'mdi',
    },
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
app.use(router);
app.use(vuetify);
app.mount('#app');