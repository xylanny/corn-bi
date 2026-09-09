<template>
  <div v-if="showed" class="auth">
    <div class="auth-container">
      <div class="auth-cancel">
        <button type="button" @click="handleCancel">&times;</button>
      </div>

      <h2 class="auth-title">{{ title }}</h2>

      <form class="auth-form" @submit.prevent="handleSubmit">
        <template v-if="mode === 'login'">
          <label class="auth-text" v-for="(_, key) in loginForm" :key="key">
            <input
              v-model="loginForm[key]"
              :type="
                ['userPassword', 'checkedPassword'].includes(key)
                  ? 'password'
                  : 'text'
              "
              :class="{ active: loginForm[key] }"
              required
            />
            <span>{{ formatHint(key) }}</span>
          </label>
        </template>

        <template v-else>
          <label class="auth-text" v-for="(_, key) in computedRegisterForm">
            <input
              v-model="registerForm[key]"
              :type="
                ['userPassword', 'checkedPassword'].includes(key)
                  ? 'password'
                  : 'text'
              "
              :class="{ active: registerForm[key] }"
              required
            />
            <span>{{ formatHint(key) }}</span>
          </label>

          <label class="auth-code">
            <input
              v-model="registerForm.emailCode"
              type="text"
              placeholder="Email Code"
            />
            <button type="button" :disabled="time > 0" @click="getCode">
              {{ time > 0 ? `${time}s` : "GET" }}
            </button>
          </label>
        </template>

        <div class="auth-actions">
          <button type="button" @click="resetForm">Reset</button>
          <button type="submit">{{ title }}</button>
        </div>

        <div class="auth-change">
          <span>{{
            mode === "login" ? "Don't have an account?" : "Have an account?"
          }}</span>
          <span @click="toggleMode">{{
            mode === "login" ? "Sign up" : "Sign in"
          }}</span>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const userStore = useUserStore();
const { user } = storeToRefs(userStore);
const { login, register, getEmailCode } = userStore;

const loginForm = ref({
  userEmail: "",
  userPassword: "",
});
const registerForm = ref({
  userName: "",
  userEmail: "",
  userPassword: "",
  checkedPassword: "",
  emailCode: "",
});
const computedRegisterForm = computed(() => {
  const { emailCode, ...rest } = registerForm.value;

  return rest;
});
function formatHint(str: string) {
  let s = str;
  if (str.toLowerCase().startsWith("user")) {
    s = str.slice(4);
  }

  const upperCaseCapital = s.charAt(0).toUpperCase();
  return upperCaseCapital + s.slice(1);
}
function resetForm() {
  loginForm.value = { userEmail: "", userPassword: "" };
  registerForm.value = {
    userName: "",
    userEmail: "",
    userPassword: "",
    checkedPassword: "",
    emailCode: "",
  };
}

const time = ref(0);
let timer: number | null = null;
function getCode() {
  if (!registerForm.value.userEmail) {
    return;
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(registerForm.value.userEmail)) {
    alert("请输入正确的邮箱格式");
    return;
  }

  try {
    // 获取邮箱验证码
    getEmailCode(registerForm.value.userEmail);
    // 开始倒计时
    startCountdown();
  } catch (error) {}
}
function startCountdown() {
  // 设置倒计时为60s
  time.value = 60;

  // 清空定时器
  if (timer) {
    window.clearInterval(timer);
    timer = null;
  }

  timer = window.setInterval(() => {
    time.value--;

    if (time.value <= 0 && timer) {
      window.clearInterval(timer);
      timer = null;
    }
  }, 1000);
}
function clearCountdown() {
  if (timer) {
    window.clearInterval(timer);
    timer = null;
  }

  time.value = 0;
}

type AuthMode = "login" | "register";
const showed = ref(true);
const route = useRoute();
const mode = ref<AuthMode>(
  route.query.mode === "register" ? "register" : "login",
);
const title = computed(() => (mode.value === "login" ? "Login" : "Register"));
function toggleMode() {
  mode.value = mode.value === "login" ? "register" : "login";

  // 重置计时器和时间
  clearCountdown();
}

// 获取路由器
const router = useRouter();

function handleCancel() {
  showed.value = false;

  router.replace("/");
}

async function handleSubmit() {
  if (mode.value === "login") {
    // 等待服务器认证
    await login(loginForm.value);
  } else {
    await register(registerForm.value);
  }

  if (user.value) {
    router.replace("/board");
  }
}
</script>

<style scoped lang="css">
.auth {
  position: fixed;
  inset: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background: var(--bg);
}

.auth-container {
  width: 25rem;
  padding: 1rem 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  border-radius: 1rem;
  background: var(--bg-lighter);
}

.auth-cancel {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}

.auth-cancel button {
  width: 2rem;
  height: 2rem;
  border: none;
  border-radius: 50%;
  font-size: 1.5rem;
  opacity: 0.9;
  background: rgba(214, 214, 214, 0.5);
  cursor: pointer;
}

.auth-title {
  padding-bottom: 1rem;
}

.auth-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.auth-text {
  position: relative;
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.auth-text input {
  min-height: 0;
  flex: 1;
  font-size: 1rem;
  border: 1px solid black;
  padding: 0.8rem 1rem;
  border-radius: 1rem;
  background: var(--bg);
}
.auth-text span {
  position: absolute;
  left: 1rem;
  top: 0.7rem;
  transition: 0.25s;
  pointer-events: none;
}
.auth-text input:focus ~ span,
.auth-text input.active ~ span {
  top: 0;
  transform: translateY(-50%) translateX(-0.4rem);
  font-size: 0.75rem;
  padding: 0.2rem 0.4rem;
  background: var(--bg);
}

.auth-code {
  width: 100%;
  display: flex;
  gap: 0.5rem;
  justify-content: center;
}
.auth-code input {
  /* 
    flex项目默认的min-width:auto; 项目不会收缩到小于其内容的最小尺寸
    设置min-width，允许flex容器收缩到内容以下
   */
  min-width: 0;
  border: 1px solid black;
  padding: 0.8rem 1rem;

  /* 
    该设置后flex项目还可以被拉伸以填充flex容器宽度
   */
  flex: 1;

  border-radius: 1rem;
  font-size: 1rem;
  background: var(--bg);
}
.auth-code button {
  padding: 0.8rem 1rem;
  border: 1px solid black;
  border-radius: 1rem;
  background: var(--bg-darker);
  color: var(--ft-clr);
  font-size: 0.9rem;
  white-space: nowrap;
  cursor: pointer;
  transition: 0.2s;
}
.auth-code button:hover:not(:disabled) {
  background-color: color-mix(in srgb, var(--bg-darkest) 90%, #000 10%);
}
.auth-code button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.auth-actions {
  display: flex;
  gap: 1rem;
}
.auth-actions button {
  outline: none;
  border: none;
  font-size: var(--ft-sz);
  padding: 0.8rem 1.2rem;
  border-radius: 0.4rem;
  background: var(--bg-darkest);
  color: var(--ft-clr);
  cursor: pointer;
}
.auth-actions button:first-child {
  background-color: color-mix(in srgb, var(--bg-darkest) 95%, black);
}
.auth-actions button:hover {
  background-color: color-mix(in srgb, var(--bg-darkest) 90%, black 10%);
}

.auth-change {
  display: flex;
  gap: 0.35rem;
  align-items: center;
  padding: 0.5rem;
}
.auth-change span:last-child {
  color: oklch(37.9% 0.146 265.522);
  cursor: pointer;
}
.auth-change span:last-child:hover {
  text-decoration: underline;
}
</style>
