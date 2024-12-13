const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  pwa: {
    name: 'Meine Wordle PWA',
    short_name: 'Wordle',
    themeColor: '#4DBA87',
    msTileColor: '#000000',
    background_color: '#ffffff',
    display: 'standalone',
    start_url: '/',
    iconPath:{
      //danach würden die iconPath kommen
    },
    workboxOptions: {
      skipWaiting: true,
      clientsClaim: true,
      runtimeCaching: [
        {
          urlPattern: /.*\.(?:css|js|html)/,  // Cache für alle Dateien mit Endungen .css, .js und .html
          handler: 'StaleWhileRevalidate',   // Strategie: Cache verwenden, während im Hintergrund aktualisiert wird
          options: {
            cacheName: 'my-pwa-cache-runtime', // Ein spezifischer Name für diesen Cache
            expiration: {
              maxEntries: 50,  // Maximale Anzahl an gespeicherten Dateien
              maxAgeSeconds: 24 * 60 * 60,  // Maximale Lebensdauer des Caches: 1 Tag
            },
          },
        },
        {
          urlPattern: /.*\.(?:png|jpg|jpeg|svg|gif)/,  // Beispiel für Bilder (optional)
          handler: 'CacheFirst', // Bei Bildern erst aus dem Cache laden, dann im Hintergrund aktualisieren
          options: {
            cacheName: 'my-pwa-cache-images',
            expiration: {
              maxEntries: 30,  // Maximale Anzahl an gespeicherten Bildern
              maxAgeSeconds: 30 * 24 * 60 * 60,  // 30 Tage
            },
          },
        },
      ]
      ,
    },
  },
});
