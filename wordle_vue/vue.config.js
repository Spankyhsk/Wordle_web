const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,


  // PWA-Konfiguration
  pwa: {
    workboxPluginMode: 'GenerateSW',  // Standardmäßig wird "InjectManifest" verwendet, aber hier deaktivieren wir das.
    workboxOptions: {
      skipWaiting: true, // Kann hilfreich sein, um den Service Worker zu aktualisieren
      clientsClaim: true, // Beansprucht sofort den Client
    },
    // Optional: Du kannst "manifestPath" konfigurieren, falls du eine benutzerdefinierte Manifestdatei hast
  },
});


