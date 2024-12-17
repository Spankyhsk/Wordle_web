const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,

  // PWA-Konfiguration
  pwa: {
    // Wir deaktivieren die automatische Service Worker-Erstellung
    serviceWorker: false,
  },
});

