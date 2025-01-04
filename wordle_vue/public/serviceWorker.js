const CACHE_NAME = 'my-vue-app-cache-v1';
// Dateien, die im Cache abgelegt werden sollen
const resourcesToCache = [
    '/',
    '/index.html',             // Startseite
    '/js/app.js',                 // Wichtige JS-Dateien
    '/js/chunk-vendors.js',
    '/favicon.ico',             // Favicon
    '/img/background.220afcc3.png',
    '/img/logo.ff041352.png',
    '/fonts/Comicmeneu-Regular.03c20265.ttf',
];


// Installations-Event: Caching der Ressourcen
self.addEventListener('install', (event) => {
    event.waitUntil(
        caches.open(CACHE_NAME).then((cache) => {
            // Hier wird überprüft, ob jede Datei abgerufen werden kann, bevor sie in den Cache eingefügt wird
            return Promise.all(
                resourcesToCache.map(url => {
                    return fetch(url).then(response => {
                        if (response.ok) {
                            cache.put(url, response); // Nur wenn die Antwort okay ist, wird sie gecacht
                        }
                    }).catch((error) => {
                        console.log(`Fehler beim Abrufen der Datei: ${url}`, error);
                    });
                })
            );
        })
    );
});

// Aktivierungs-Event: Service Worker übernimmt die Kontrolle
self.addEventListener('activate', (event) => {
    event.waitUntil(
        self.clients.claim() // Macht den neuen Service Worker sofort aktiv
    );
});

self.addEventListener('fetch', (event) => {
    event.respondWith(
        caches.match(event.request).then((cachedResponse) => {
            if (cachedResponse) {
                return cachedResponse;  // Wenn die Datei im Cache ist, wird sie verwendet
            }

            // Andernfalls wird die Datei aus dem Netzwerk geladen
            return fetch(event.request).catch(() => {
                // Wenn der Benutzer offline ist, zeige die index.html
                return caches.match('/index.html');  // Die index.html aus dem Cache zurückgeben
            });
        })
    );
});