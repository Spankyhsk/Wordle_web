const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,

  // PWA-Konfiguration
  pwa: {
    workboxPluginMode: 'GenerateSW',  // Standardmäßig wird "GenerateSW" verwendet
    workboxOptions: {
      skipWaiting: true,  // Der Service Worker wird sofort aktiv
      clientsClaim: true, // Macht den Service Worker sofort aktiv
    },
  },
});


