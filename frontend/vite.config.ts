import vue from "@vitejs/plugin-vue";
import { defineConfig } from "vite";

// https://vite.dev/config/
export default defineConfig({
  resolve: {
    alias: {
      "@": "/src",
    },
  },

  plugins: [vue()],

  server: {
    host: "0.0.0.0",
    port: 3000,
  },

  envPrefix: ["VITE_", "APP_"],
});
