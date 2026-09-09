<template>
  <div class="bi">
    <div class="main">
      <RouterView />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import { useThemeStore } from "./stores/theme";

const themeStore = useThemeStore();
const { applyTheme } = themeStore;

onMounted(() => {
  applyTheme();
});
</script>

<style scoped lang="css">
.bi {
  width: 100%;
  min-height: 100vh;
}
</style>
