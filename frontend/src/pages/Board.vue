<template>
  <div class="board">
    <aside
      class="board-sidebar"
      :class="{ opened: isOpen }"
      :style="{ width: isOpen ? '200px' : '54px' }"
    >
      <div class="sidebar-header"></div>
      <nav class="sidebar-nav">
        <li
          v-for="{ id, icon, label, badge } in navItems"
          :key="id"
          class="nav-item"
          :class="{ active: activeItemId === id }"
        >
          <a href="#" class="nav-link" @click.prevent="toggleActiveItem(id)">
            <!-- 采用v-html指令渲染SVG图标 -->
            <span class="nav-icon" v-html="icon"></span>
            <span class="nav-label" v-if="isOpen">{{ label }}</span>
            <span class="nav-badge" v-if="isOpen">{{ badge }}</span>
          </a>
        </li>
      </nav>
      <div class="sidebar-footer">
        <button class="footer-toggle" @click="toggleOpen">
          <svg width="32" height="32" viewBox="0 0 20 20" fill="var(--clr)">
            <path
              d="M15 5L10 10L15 15"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              :style="{
                transform: isOpen ? 'rotate(180deg)' : 'none',
              }"
            />
            <path
              d="M5 5L5 15"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
            />
          </svg>
        </button>
      </div>
    </aside>

    <main class="board-main">
      <div class="main-header">
        <div class="header-avatar" @click="goToUserPage">
          <span v-if="!user?.userAvatar">?</span>
          <img
            v-else
            :src="`data:image/jpeg;base64,${user.userAvatar}`"
            alt="user avatar"
          />
        </div>
      </div>

      <div class="main-content">
        <RouterView />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { ref } from "vue";
import { useRouter } from "vue-router";

interface NavItem {
  id: number;
  label: string;
  icon: string;
  badge?: string | number;
}

const isOpen = ref(true);
const navItems = ref<NavItem[]>([
  {
    id: 1,
    label: "Chart",
    icon: `<svg t="1788491068744" class="icon" viewBox="0 0 1080 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="19384" width="20" height="20"><path d="M265.216 920.348444a41.472 41.472 0 0 1-41.415111-42.211555v-215.608889a42.154667 42.154667 0 1 1 83.512889 0v215.608889a42.097778 42.097778 0 0 1-42.097778 42.211555z m207.701333 0a41.472 41.472 0 0 1-41.358222-42.211555V536.689778a42.097778 42.097778 0 0 1 83.512889 0v340.764444a42.097778 42.097778 0 0 1-42.097778 42.894222z m207.758223 0a41.472 41.472 0 0 1-41.358223-42.211555V728.177778a42.097778 42.097778 0 0 1 83.512889 0v149.959111a42.097778 42.097778 0 0 1-42.097778 42.211555z m207.758222 0a41.472 41.472 0 0 1-41.415111-42.211555v-276.48a42.154667 42.154667 0 1 1 83.569777 0v279.950222a42.097778 42.097778 0 0 1-42.097777 38.684445zM210.659556 502.101333a41.415111 41.415111 0 0 1-33.792-16.554666 42.211556 42.211556 0 0 1 10.353777-58.766223l300.202667-231.537777a42.723556 42.723556 0 0 1 51.768889 0l138.069333 112.64 222.947556-185.969778a41.756444 41.756444 0 0 1 53.816889 63.601778l-247.808 208.782222a41.358222 41.358222 0 0 1-53.134223 0L515.072 280.917333 238.933333 493.112889a42.723556 42.723556 0 0 1-28.273777 8.988444z" fill="var(--clr)" p-id="19385"></path><path d="M927.118222 336.896a42.097778 42.097778 0 0 1-42.097778-41.472V179.313778h-117.361777a42.154667 42.154667 0 0 1 0-83.626667h159.459555a42.097778 42.097778 0 0 1 42.097778 41.415111v158.321778a42.097778 42.097778 0 0 1-42.097778 41.528889z" fill="var(--clr)" p-id="19386"></path></svg>`,
    badge: "beta",
  },
  {
    id: 2,
    label: "Setting",
    icon: `<svg t="1788490935449" class="icon" viewBox="0 0 1084 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="16871" width="20" height="20"><path d="M1072.147851 406.226367c-6.331285-33.456782-26.762037-55.073399-52.047135-55.073399-0.323417 0-0.651455 0.003081-0.830105 0.009241l-4.655674 0c-73.124722 0-132.618162-59.491899-132.618162-132.618162 0-23.731152 11.447443-50.336101 11.546009-50.565574 13.104573-29.498767 3.023185-65.672257-23.427755-84.127081l-1.601687-1.127342-134.400039-74.661726-1.700252-0.745401c-8.753836-3.805547-18.334698-5.735272-28.479231-5.735272-20.789593 0-41.235746 8.344174-54.683758 22.306575-14.741683 15.216028-65.622973 58.649474-104.721083 58.649474-39.450789 0-90.633935-44.286652-105.438762-59.784516-13.518857-14.247316-34.128258-22.753199-55.127302-22.753199-9.945862 0-19.354234 1.861961-27.958682 5.531982l-1.746455 0.74078-139.141957 76.431283-1.643269 1.139662c-26.537186 18.437884-36.675557 54.579032-23.584845 84.062398 0.115506 0.264895 11.579891 26.725075 11.579891 50.634877 0 73.126262-59.491899 132.618162-132.618162 132.618162l-4.581749 0c-0.318797-0.00616-0.636055-0.01078-0.951772-0.01078-25.260456 0-45.672728 21.618157-52.002472 55.0811-0.462025 2.453354-11.313456 60.622322-11.313456 106.117939 0 45.494078 10.85143 103.659965 11.314996 106.119479 6.334365 33.458322 26.758957 55.076479 52.036353 55.076479 0.320337 0 0.651455-0.00616 0.842426-0.012321l4.655674 0c73.126262 0 132.618162 59.491899 132.618162 132.616622 0 23.760413-11.444363 50.333021-11.546009 50.565574-13.093793 29.474125-3.041666 65.646075 23.395414 84.151722l1.569346 1.093459 131.838879 73.726895 1.675611 0.7377c8.750757 3.84251 18.305437 5.790715 28.397607 5.790715 21.082208 0 41.676209-8.706094 55.0888-23.290689 18.724339-20.347588 69.527086-62.362616 107.04815-62.362616 40.625872 0 92.72537 47.100385 107.759669 63.583903 13.441852 14.831008 34.176001 23.689571 55.470741 23.695731l0.00616 0c9.895039 0 19.27877-1.883523 27.893999-5.598205l1.711034-0.73924 136.659342-75.531873 1.617088-1.128882c26.492523-18.456365 36.601633-54.600594 23.538642-84.016195-0.115506-0.267974-11.595291-27.082374-11.595291-50.67646 0-73.124722 59.49344-132.616622 132.618162-132.616622l4.517066-0.00154c0.300316 0.00616 0.599092 0.009241 0.899409 0.009241 25.331299-0.00154 45.785153-21.619697 52.107197-55.054918 0.112426-0.589852 11.325776-59.507301 11.325776-106.14104C1083.464388 466.640776 1072.609877 408.67356 1072.147851 406.226367zM377.486862 945.656142l-115.32764-64.487932c5.082277-13.052211 15.437801-43.51815 15.437801-75.017486 0-109.382917-84.176364-199.816642-192.587488-208.134635-2.647404-15.427021-8.873963-54.967133-8.873963-85.667166 0-30.65691 6.223479-70.232445 8.869343-85.671786 108.415744-8.311832 192.592108-98.745557 192.592108-208.134635 0-31.416171-10.300081-61.797405-15.371577-74.854236l122.721583-67.40331c0.003081 0 0.00462 0.00154 0.007701 0.00154 4.423121 4.518606 22.121764 22.080182 46.558275 39.493911 39.929754 28.46229 77.952885 42.894416 113.014434 42.894416 34.716571 0 72.437845-14.151831 112.115025-42.06431 24.282503-17.07953 41.896442-34.302288 46.308782-38.74543 0.009241-0.00154 0.018481-0.00462 0.026182-0.00616l118.301542 65.726159c-5.077657 13.055291-15.416239 43.499669-15.416239 74.958962 0 109.389077 84.174824 199.822802 192.590568 208.134635 2.645865 15.462442 8.872423 55.107281 8.872423 85.671786 0 30.687711-6.223479 70.241685-8.869343 85.673326C890.042174 606.334084 805.86427 696.767809 805.86427 806.158426c0 31.450053 10.317022 61.851309 15.393138 74.903519l-119.783103 66.198965c-5.168521-5.490399-22.603811-23.363073-46.740005-41.288109-40.701336-30.224145-79.662378-45.549521-115.800446-45.549521-35.79155 0-74.458435 15.038919-114.927219 44.694774C400.22004 922.554885 382.666163 940.255068 377.486862 945.656142zM731.271848 511.646647c0-105.803762-86.081448-191.88059-191.888289-191.88059-105.803762 0-191.88059 86.076827-191.88059 191.88059 0 105.803762 86.076827 191.882129 191.88059 191.882129C645.19194 703.528777 731.271848 617.450409 731.271848 511.646647zM539.383558 395.903184c63.825696 0 115.751164 51.922387 115.751164 115.743463 0 63.825696-51.925468 115.751164-115.751164 115.751164-63.821076 0-115.743463-51.925468-115.743463-115.751164C423.640095 447.824031 475.562482 395.903184 539.383558 395.903184z" fill="var(--clr)" p-id="16872"></path></svg>`,
  },
  {
    id: 3,
    label: "Table",
    icon: `<svg t="1788932173626" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="7450" id="mx_n_1788932173626" width="20" height="20" fill="var(--clr)"><path d="M836.4032 33.1776H187.5968C102.7072 33.1776 33.792 102.0928 33.792 186.9824v649.9328c0 84.8896 68.9152 153.8048 153.8048 153.8048h648.8064c84.8896 0 153.8048-68.9152 153.8048-153.8048V186.9824c0-84.8896-68.9152-153.8048-153.8048-153.8048zM329.9328 923.4432h-142.336c-47.616 0-86.528-38.7072-86.528-86.528v-152.576h228.864v239.104z m0-306.3808H101.0688V379.1872h228.864v237.8752z m296.448 306.3808H397.2096V684.3392h229.1712v239.104z m0-306.3808H397.2096V379.1872h229.1712v237.8752z m296.5504 219.9552c0 47.616-38.7072 86.528-86.528 86.528H693.6576V684.3392h229.2736v152.6784z m0-219.9552H693.6576V379.1872h229.2736v237.8752z m0-305.2544H101.0688V186.9824c0-47.616 38.7072-86.528 86.528-86.528h648.8064c47.616 0 86.528 38.7072 86.528 86.528v124.8256z" p-id="7451"></path></svg>`,
  },
  {
    id: 5,
    label: "K-Means",
    icon: `<svg t="1788591453088" class="icon" viewBox="0 0 1025 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6040" width="20" height="20" fill="var(--clr)"><path d="M957.187413 725.205333a125.994667 125.994667 0 0 0-152.106666 15.189334l-64.64-38.016v-178.688a57.984 57.984 0 0 0-26.624-45.610667L554.115413 386.816V306.986667a133.077333 133.077333 0 1 0-83.626666 0v79.829333l-159.701334 91.264a52.736 52.736 0 0 0-26.624 45.610667v178.688l-64.64 38.016a125.994667 125.994667 0 0 0-152.106666-15.189334 133.717333 133.717333 0 1 0 197.717333 91.264l57.045333-34.133333 167.296 95.061333a54.954667 54.954667 0 0 0 26.624 7.594667 70.485333 70.485333 0 0 0 26.624-7.594667l167.210667-95.104 57.045333 34.133334a135.381333 135.381333 0 0 0 64.64 140.8 132.266667 132.266667 0 0 0 182.485334-49.408 149.034667 149.034667 0 0 0-56.917334-182.613334z m-802.133333 163.498667a55.466667 55.466667 0 1 1 19.029333-76.032 52.352 52.352 0 0 1-19.242666 76.032z m296.576-710.997333a57.045333 57.045333 0 1 1 57.045333 57.045333 58.496 58.496 0 0 1-57.258666-57.045333z m201.514667 532.309333l-144.469334 83.669333-144.554666-83.669333V542.72l144.469333-83.669333 144.469333 83.669333z m285.141333 155.904a56.618667 56.618667 0 0 1-76.032 22.826667 52.352 52.352 0 0 1-19.029333-76.032 56.618667 56.618667 0 0 1 76.032-22.826667 55.466667 55.466667 0 0 1 18.816 75.989333z" p-id="var(----clr)"></path></svg>`,
  },
]);
const activeItemId = ref(1);

function toggleOpen() {
  isOpen.value = !isOpen.value;
}

// 获取路由器
const router = useRouter();
function toggleActiveItem(id: number) {
  // 更新获取导航项
  activeItemId.value = id;

  const activeItem = navItems.value.find((item) => item.id === id);

  // console.log(activeItem);

  // 导航到子路由
  router.replace(`/board/${activeItem?.label.toLowerCase()}`);
}

// 获取应用共享用户信息
const userStore = useUserStore();
const { user } = storeToRefs(userStore);

function goToUserPage() {
  router.replace(`/profile`);
}
</script>

<style scoped lang="css">
.board {
  height: 100vh;
  width: 100%;

  display: flex;
  background-color: var(--bg);
  font-family:
    -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.active {
  background: var(--bg-darkest);
}

.board-sidebar {
  z-index: 5;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  border-right: var(--border);

  display: flex;
  flex-direction: column;

  overflow: hidden;
  background: var(--bg-darker);
  transition: var(--transition);
}

.sidebar-header {
  height: 48px;
  border-bottom: var(--border);

  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.sidebar-nav {
  padding: 12px 8px;

  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;

  list-style: none;
  overflow-y: auto;
}
.nav-item {
  border-radius: 8px;

  transition: background 0.15s;
}
.nav-item.active .nav-link {
  color: rgb(86, 86, 85);
  font-weight: 500;
}
.nav-link {
  padding: 8px 12px;

  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;

  color: var(--clr);
  font-size: 14px;
  white-space: nowrap;
  text-decoration: none;
  border-radius: 8px;

  transition:
    background 0.15s,
    color 0.15s;
}
.nav-link:hover {
  background: var(--bg-darkest);
  color: var(--clr);
}
.nav-icon {
  width: 20px;
  height: 20px;

  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;

  color: currentColor;
}
.nav-icon svg {
  width: 20px;
  height: 20px;

  display: block;
}
.nav-label {
  flex: 1;
}
.nav-badge {
  background: var(--primary);
  color: --clr;
  font-size: 11px;
  font-weight: 500;
  padding: 1px 8px;
  border-radius: 12px;
  line-height: 18px;
  letter-spacing: 0.3px;
}

.sidebar-footer {
  margin-top: auto;
  border-top: var(--border);
  padding: 8px 8px;

  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 12px;
}
.footer-toggle {
  border: none;

  display: flex;
  align-items: center;
  justify-content: center;

  color: var(--clr);
  cursor: pointer;
  border-radius: 8px;
  background: var(--bg-darkest);

  transition:
    background 0.2s,
    color 0.2s;
}

.board-main {
  flex: 1;
  width: 100%;
  margin-left: 54px;
  transition: margin-left var(--transition);
  min-height: 0;
  height: 100vh;

  display: flex;
  flex-direction: column;

  overflow: hidden;
  transition: var(--transition);
}
.board-sidebar.opened ~ .board-main {
  margin-left: 200px;
}

.main-header {
  width: 100%;
  height: 48px;
  padding: 0 var(--padding);
  border-bottom: var(--border);

  display: flex;
  justify-content: flex-end;
  align-items: center;

  background: var(--bg-darker);
}
.header-avatar {
  width: 36px;
  height: 36px;
  border: var(--border);

  flex-shrink: 0;

  border-radius: 50%;
  overflow: hidden;
  background: var(--border);
  cursor: pointer;

  transition: box-shadow 0.3s ease;
}
.header-avatar:hover {
  box-shadow: 0 0 2px black;
}
.header-avatar span {
  width: 36px;
  height: 36px;

  display: flex;
  justify-content: center;
  align-items: center;
}
.header-avatar img {
  width: 36px;
  height: 36px;
  object-fit: cover;
}
.header-info {
  display: flex;
  flex-direction: column;

  line-height: 1.3;
  overflow: hidden;
}
.header-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--clr);
}
.header-role {
  font-size: 12px;
  color: var(--clr);
}

.main-content {
  min-height: 0;

  flex: 1;

  overflow: hidden;
}
</style>
