const { defineConfig } = require('@vue/cli-service')
const webpack = require('webpack')

module.exports = defineConfig({
  transpileDependencies: true,

  configureWebpack: {
    plugins: [
      new webpack.DefinePlugin({
        'process.env': {
          NODE_ENV: JSON.stringify(process.env.NODE_ENV || 'production'),
          BASE_URL: JSON.stringify(process.env.BASE_URL || '/')
        }
      })
    ]
  },

  pwa: {
    name: 'Meine Wordle App',
    themeColor: '#4DBA87',
    msTileColor: '#000000',
    manifestOptions: {
      background_color: '#FFFFFF'
    },
    workboxOptions: {
      runtimeCaching: [
        {
          urlPattern: new RegExp('^https://api.example.com/'),
          handler: 'NetworkFirst',
          options: {
            cacheName: 'api-cache',
            expiration: {
              maxEntries: 50,
              maxAgeSeconds: 86400
            },
            cacheableResponse: {
              statuses: [0, 200]
            }
          }
        }
      ]
    }
  }
})
