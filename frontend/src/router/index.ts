import { useUserStore } from "@/stores/user";
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
    // 将查询参数变成组件props
    props: (route) => ({ query: route.query }),
  },
  {
    path: "/board",
    name: "board",
    component: () => import("@/pages/Board.vue"),
    redirect: "/board/table",

    children: [
      {
        path: "setting",
        component: () => import("@/pages/Setting.vue"),
      },
      {
        path: "table",
        component: () => import("@/pages/Table.vue"),
      },
      {
        path: "k-means",
        component: () => import("@/pages/KMeans.vue"),
      },
      {
        path: "chart",
        component: () => import("@/pages/Chart.vue"),
      },
    ],
  },
  {
    path: "/profile",
    name: "profile",
    component: () => import("@/pages/Profile.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(), //采用history模式
  routes,
});

const whiteList = ["/", "/auth"];

router.beforeEach(async (to) => {
  const userStore = useUserStore();

  const isAuthenticated = await userStore.resume();

  if (isAuthenticated && whiteList.includes(to.path)) {
    return "/board";
  }

  if (!isAuthenticated && !whiteList.includes(to.path)) {
    return "/auth";
  }

  return true;
});

export default router;
