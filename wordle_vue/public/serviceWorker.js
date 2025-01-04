const CACHE_NAME = 'my-vue-app-cache-v2'; // Sicherstellen, dass die Version des Caches geändert wird
const resourcesToCache = [
    '/',
    '/index.html',
    '/js/app.js',
    '/js/chunk-vendors.js',
    '/favicon.ico',
    '/img/background.220afcc3.png',
    '/img/logo.ff041352.png',
    '/fonts/Comicmeneu-Regular.03c20265.ttf',
];

// Installations-Event: Ressourcen werden gecacht
self.addEventListener('install', (event) => {
    console.log('Service Worker installed');
    event.waitUntil(
        caches.open(CACHE_NAME).then((cache) => {
            // Nur die Ressourcen cachen, die nicht bereits im Cache sind
            return cache.addAll(resourcesToCache);
        })
    );
});

// Aktivierungs-Event: Alten Cache löschen, wenn eine neue Version da ist
self.addEventListener('activate', (event) => {
    console.log('Service Worker activated');
    const cacheWhitelist = [CACHE_NAME]; // Nur den aktuellen Cache behalten
    event.waitUntil(
        caches.keys().then((cacheNames) => {
            return Promise.all(
                cacheNames.map((cacheName) => {
                    if (!cacheWhitelist.includes(cacheName)) {
                        return caches.delete(cacheName); // Lösche alte Caches
                    }
                })
            );
        }).then(() => {
            // Nimm sofort die Kontrolle über die Seite
            self.clients.claim();
        })
    );
});

// Fetch-Event: Zuerst aus dem Cache, dann aus dem Netzwerk
self.addEventListener('fetch', (event) => {
    event.respondWith(
        caches.match(event.request).then((cachedResponse) => {
            if (cachedResponse) {
                return cachedResponse; // Wenn die Datei im Cache ist, wird sie verwendet
            }

            // Andernfalls wird die Datei aus dem Netzwerk geladen
            return fetch(event.request).then((response) => {
                // Cache die Antwort nur, wenn sie erfolgreich ist
                if (response && response.status === 200) {
                    const clonedResponse = response.clone();
                    caches.open(CACHE_NAME).then((cache) => {
                        cache.put(event.request, clonedResponse); // Antwort in den Cache legen
                    });
                }
                return response;
            }).catch(() => {
                // Bei Offline-Nutzung eine fallback-Seite anbieten
                return caches.match('/index.html');
            });
        })
    );
});
