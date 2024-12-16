import { createApp } from 'vue';
import App from './App.vue';

// Vuetify
import 'vuetify/styles';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';

//routes
import router from './router'; // Router importieren

import './registerServiceWorker';

// Vuetify-Instanz erstellen
const vuetify = createVuetify({
    components,
    directives,
});

// Vue App initialisieren
const app = createApp(App);
app.use(router)
app.use(vuetify);
app.mount('#app');