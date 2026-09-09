import {
  createRouter,
  createWebHistory,
  type RouteRecordRaw,
} from "vue-router";

const routes: RouteRecordRaw[] = [
  {
    path: "/",
    name: "root",
    component: () => import("@/pages/Welcome.vue"),
  },
  {
    path: "/auth",
    name: "auth",
    component: () => import("@/pages/Auth.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(), //采用history模式
  routes,
});

export default router;
