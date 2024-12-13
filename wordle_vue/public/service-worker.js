self.addEventListener('install', (event) => {
    console.log('Service Worker: Install');
    // Cache files during installation
    event.waitUntil(
        caches.open('my-pwa-cache').then((cache) => {
            return cache.addAll([
                '/', // Home page
                '/index.html', // Index HTML
                '/favicon.ico' // Favicon
            ]);
        })
    );
});

self.addEventListener('activate', (event) => {
    console.log('Service Worker: Activate');
    // Clear old caches
    event.waitUntil(
        caches.keys().then((cacheNames) => {
            return Promise.all(
                cacheNames.map((cacheName) => {
                    if (cacheName !== 'my-pwa-cache') {
                        console.log('Service Worker: Clearing Old Cache', cacheName);
                        return caches.delete(cacheName);
                    }
                })
            );
        })
    );
});

self.addEventListener('fetch', (event) => {
    console.log('Service Worker: Fetch', event.request.url);

    // Try fetching from the network, and fallback to cache if it fails
    event.respondWith(
        caches.match(event.request).then((cachedResponse) => {
            return (
                cachedResponse ||
                fetch(event.request).then((networkResponse) => {
                    // Cache the network response
                    caches.open('my-pwa-cache').then((cache) => {
                        cache.put(event.request, networkResponse.clone());
                    });
                    return networkResponse;
                })
            );
        })
    );
});