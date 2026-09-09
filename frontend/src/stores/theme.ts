import { defineStore } from "pinia";
import { usePreferredDark, useStorage } from "@vueuse/core";
import { computed, watch } from "vue";

type ThemeMode = "light" | "dark" | "system";

export const useThemeStore = defineStore("theme", () => {
  // 获取系统主题偏好
  // const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
  const systemPrefersDark = usePreferredDark();
  // 持久化
  const mode = useStorage<ThemeMode>("theme-mode", "system");
  const actualTheme = computed(() => {
    if (mode.value === "system") {
      return systemPrefersDark.value ? "dark" : "light";
    } else {
      return mode.value;
    }
  });

  const isDark = computed(() => actualTheme.value === "dark");
  const isLight = computed(() => actualTheme.value === "light");
  const isSystem = computed(() => mode.value === "system");

  function setTheme(newMode: ThemeMode) {
    mode.value = newMode;

    applyTheme();
  }

  function applyTheme() {
    const html = document.documentElement;

    if (actualTheme.value === "dark") {
      html.classList.add("dark");
    } else {
      html.classList.remove("dark");
    }
  }

  watch(systemPrefersDark, () => {
    if (mode.value === "system") {
      applyTheme();
    }
  });

  // 初始化
  applyTheme();

  return {
    mode,
    actualTheme,
    isDark,
    isLight,
    isSystem,
    setTheme,
  };
});
