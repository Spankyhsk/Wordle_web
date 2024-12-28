import { createRouter, createWebHistory } from 'vue-router';
import RulePage from '../pages/rulePage.vue';
import SoloPage from '../pages/WordleSoloPage.vue';
import MultiPage from '../pages/WordleMultiPage.vue';
import OfflinePage from '../pages/OfflinePage.vue';
import NotFound from '../pages/NotFound.vue';

const routes = [
    { path: '/', component: RulePage },
    { path: '/solo', component: SoloPage },
    { path: '/multi', component: MultiPage },
    { path: '/offline', component: OfflinePage },
    { path: '/:pathMatch(.*)*', component: NotFound } // 404-Seite
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;