import {
    createRouter,
    createWebHashHistory,
    createWebHistory,
} from "vue-router";

import { useUserStore } from "@/stores/modules/user";

import routes from "./routes";

const router = createRouter({
    // history: createWebHistory(import.meta.env.BASE_URL),
    history: createWebHashHistory(),
    routes,
});

router.beforeEach((to, from, next) => {
    const userStore = useUserStore();
    if (
        to.name !== "login" &&
        to.name !== "loginAdmin" &&
        to.name !== "register" &&
        !userStore.access_token
    )
        next({ name: "login" });
    else next();
    // next();
});

export default router;
