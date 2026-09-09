<template>
  <div class="setting">
    <div class="setting-theme">
      <span class="theme-label">Theme:</span>
      <div class="theme-select">
        <button type="button" @click="isThemeMenuOpen = !isThemeMenuOpen">
          {{ curTheme }}
        </button>

        <div class="select-menu" v-if="isThemeMenuOpen">
          <button
            v-for="(value, key) in THEMES"
            :key="key"
            type="button"
            class="menu-option"
            :class="{ selected: mode === value }"
            @click="selectTheme(value)"
          >
            {{ value }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useThemeStore } from "@/stores/theme";
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

const themeStore = useThemeStore();
const { mode } = storeToRefs(themeStore);
const { setTheme } = themeStore;
const isThemeMenuOpen = ref(false);
const THEMES = {
  system: "system",
  light: "light",
  dark: "dark",
} as const;
const curTheme = computed(() =>
  Object.keys(THEMES).find((k) => k === mode.value),
);

function selectTheme(value: "system" | "light" | "dark") {
  setTheme(value);
  isThemeMenuOpen.value = false;
}
</script>

<style scoped lang="css">
.setting {
  width: 100%;
  height: 100%;
  padding: var(--padding);

  column-count: 2;
  column-gap: var(--ft-sz);
}
.setting > * {
  break-inside: avoid;
  padding: 8px 16px;
}

.setting-theme {
  border: var(--border);
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.theme-label {
  color: var(--clr);
}
.theme-select {
  position: relative;
  min-width: 120px;
}
.theme-select button {
  width: 100%;
  padding: var(--padding);

  color: var(--clr);
  text-align: center;
  cursor: pointer;
  background: var(--bg-darker);
  border-radius: var(--radius);
}
.select-menu {
  position: absolute;
  z-index: 1;
  top: 100%;
  right: 0;
  left: 0;
  padding: var(--padding-smallest);
  border: var(--border);

  display: flex;
  flex-direction: column;
  gap: var(--gap-smaller);

  border-radius: var(--radius);
  background: var(--bg-lighter);

  transition: var(--transition);
}
.select-menu button {
  padding: var(--padding);
  border-radius: var(--radius);
  border: none;
}
.select-menu button:hover,
.menu-option.selected {
  background: var(--bg-darkest);
}

@media (max-width: 767px) {
  .setting {
    column-count: 1;
  }
}
</style>
