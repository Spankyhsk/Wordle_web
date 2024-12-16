const CACHE_NAME = 'my-app-cache-v1'; // Cache-Name für diese Version
const ASSETS_TO_CACHE = [
    '/', // Startseite
    '/pages/OfflinePage.vue',
];

// Installationsereignis: Caching von Ressourcen
self.addEventListener('install', (event) => {
    console.log('Service Worker: Installationsereignis');

    // Ressourcen cachen
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then((cache) => {
                console.log('Service Worker: Caching von Ressourcen');
                return cache.addAll(ASSETS_TO_CACHE);
            })
    );
});

// Aktivierungsereignis: Alte Caches löschen
self.addEventListener('activate', (event) => {
    console.log('Service Worker: Aktivierungsereignis');

    // Entferne alte Caches
    event.waitUntil(
        caches.keys().then((cacheNames) => {
            return Promise.all(
                cacheNames.map((cacheName) => {
                    if (cacheName !== CACHE_NAME) {
                        console.log(`Service Worker: Lösche alten Cache ${cacheName}`);
                        return caches.delete(cacheName);
                    }
                })
            );
        })
    );
});

// Fetch-Ereignis: Abfangen von Anfragen und Bereitstellung des Caches
self.addEventListener('fetch', (event) => {
    console.log('Service Worker: Fetch-Ereignis', event.request.url);

    event.respondWith(
        caches.match(event.request).then((cachedResponse) => {
            // Wenn eine Antwort im Cache gefunden wird, verwenden wir sie
            if (cachedResponse) {
                console.log('Service Worker: Rückgabe aus Cache', event.request.url);
                return cachedResponse;
            }

            // Andernfalls versuchen wir, die Ressource vom Netzwerk zu holen
            console.log('Service Worker: Hole aus dem Netzwerk', event.request.url);
            return fetch(event.request).then((networkResponse) => {
                // Wenn die Netzwerk-Antwort erfolgreich ist, speichern wir sie im Cache
                return caches.open(CACHE_NAME).then((cache) => {
                    cache.put(event.request, networkResponse.clone());
                    return networkResponse;
                });
            });
        }).catch((error) => {
            // Fehlerbehandlung, falls etwas schiefgeht (z.B. beim Abfangen von Anfragen im Offline-Modus)
            console.error('Service Worker: Fehler bei der Verarbeitung der Anfrage', error);
        })
    );
});