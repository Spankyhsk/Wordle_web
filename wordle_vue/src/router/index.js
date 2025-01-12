import { createRouter, createWebHistory } from 'vue-router';
import RulePage from '../pages/rulePage.vue';
import SoloPage from '../pages/WordleSoloPage.vue';
import MultiPage from '../pages/WordleMultiPage.vue';
import OfflinePage from '../pages/OfflinePage.vue';
import NotFound from '../pages/NotFound.vue';
import LoginPage from '../pages/LoginPage.vue';
import { auth } from "@/firebase";



const routes = [
    { path: '/', component: RulePage },
    { path: '/login', component: LoginPage},
    { path: '/solo', component: SoloPage },
    { path: '/multi', component: MultiPage, meta: { requiresAuth: true } },
    { path: '/offline', component: OfflinePage },
    { path: '/:pathMatch(.*)*', component: NotFound } // 404-Seite
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach((to, from, next) => {
    const user = auth.currentUser; // Prüfe den aktuellen Benutzer
    if (to.meta && to.meta.requiresAuth && !user) {
        next("/login"); // Weiterleitung zur Login-Seite
    } else {
        next(); // Zugriff erlauben
    }
});

export default router;