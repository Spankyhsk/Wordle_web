const { defineConfig } = require('@vue/cli-service')
const webpack = require('webpack')

module.exports = defineConfig({
  transpileDependencies: true,


  pwa: {
    name: 'My App',
    themeColor: '#42b983',
    display: 'standalone',
    start_url: '/',
    workboxOptions: {
      // Der Cache-Name wird automatisch erstellt, du kannst ihn jedoch explizit setzen:
      cacheId: 'my-app-cache',

      // Die `precacheManifestFilename` Option ist veraltet und wird nicht mehr verwendet.
      // Entferne sie und verwende optional `additionalManifestEntries`:
      additionalManifestEntries: [
        '/main.js',
        '/pages/OfflinePage.vue',
      ],

      runtimeCaching: [
        {
          urlPattern: /\/pages\/OfflinePage\.vue$/,
          handler: 'CacheFirst',
          options: {
            cacheName: 'static-assets',
            expiration: {
              maxEntries: 50,
              maxAgeSeconds: 30 * 24 * 60 * 60 // 30 days
            }
          }
        },
      ],
      // Erlaubt es dem neuen Service Worker, sofort zu übernehmen:
      skipWaiting: true,
      clientsClaim: true
    }
  }
})
