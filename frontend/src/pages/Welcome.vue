<template>
  <div class="welcome">
    <header class="welcome-heading">
      <RouterLink class="header-brand" to="/">
        <svg
          t="1788752219799"
          class="icon"
          viewBox="0 0 1024 1024"
          version="1.1"
          xmlns="http://www.w3.org/2000/svg"
          p-id="15385"
          width="20"
          height="20"
        >
          <path
            d="M892.047 0c-25.9 0-50.54 7.6-71.34 21.24-30.7-8.16-56.3-3-69.78 0.98a130.954 130.954 0 0 0-55.22 31.9c-12.48-0.88-35.1-0.78-60.94 10.2-20.04 9.02-37.04 22.26-50.08 38.4a129.988 129.988 0 0 0-59.36 17.98c-17.5 9.92-32.24 23.96-43.22 40.64-20.36 2.98-39.66 10.74-56.64 22.86-0.18 0.12-0.3 0.32-0.48 0.46-13.92-19.08-28.96-37.48-45.98-54.48l-42.26-42.28c-18.078-18.08-48.998-8.72-53.998 16.34l-47.52 237.62-161.1 161.12C33.97 543.16 5.97 595.3 0.95 651.88c-6.2 69.82 18.18 136.56 67 185.38l124.42 124.46 0.56-0.56c54.76 51.138 129.54 73.798 206.62 57.758 45.72-9.5 86.84-34.5 119.86-67.52l162.64-162.68 237.6-47.52c25.06-5.02 34.42-35.94 16.36-54l-42.28-42.28c-17.32-17.32-36.04-32.62-55.48-46.72 9.92-15.5 16.86-32.42 19.58-50.12a129.478 129.478 0 0 0 41.88-44.48c10.58-17.8 16.52-37.68 17.68-57.88 16.22-12.82 29.36-29.4 38.04-48.7 8.94-19.86 12.66-41.16 11.26-62.1a133.09 133.09 0 0 0 32.239-53.94 132.434 132.434 0 0 0 1.58-69.02C1056.527 130.4 1009.307 0 892.047 0zM113.21 554.42L294.19 373.4l39.58-197.98c96.66 96.66 130.7 230.9 105.02 355.44-70.94 21.44-135.96 60.18-189.84 114.08L108.129 785.8c-59.92-65.96-58.6-167.72 5.08-231.38z m735.26 135.76l-197.96 39.6-181 181.02c-32.8 32.8-75.78 49.2-118.78 49.2-42.98 0-85.98-16.4-118.78-49.2l-79.18-79.2 141.4-141.42c76.54-76.54 176.84-114.82 277.14-114.82 100.32 0 200.62 38.28 277.16 114.82z m74.32-492.5c15 16.62 21.96 40.72 15 64.3-6.98 22.5-24.66 39.1-45.54 45.52 11.76 18.22 14.46 41.8 4.82 63.22-9.66 21.42-28.92 34.82-50.34 38.58 9.66 19.28 9.1 42.84-2.66 62.66-11.26 19.82-31.62 31.6-53.04 33.22 7.16 18.9 5.22 40.64-6.58 58.9-64.88-34.4-137.58-52.72-213.1-52.72-21.64 0-42.92 2.02-63.96 4.96 3.06-20.96 5.2-42.16 5.2-63.74 0-75.58-18.32-148.32-52.76-213.26 26.96-22.44 55.16-14.82 64.98-11.12 1.62-20.88 13.4-41.26 33.22-52.5 31.64-18.78 61.84-3.06 62.66-2.66 3.76-20.9 17.68-40.18 39.1-49.82 26.92-11.42 50.86-2.64 63.22 5.36 6.44-21.42 23.56-38.56 46.06-45 6-1.78 37.28-10.06 64.28 16.06C844.67 77.98 866.89 64 892.05 64c1.04 0 67.7-1.9 67.7 75.84-1.08 25.7-16.06 47.12-36.96 57.84z"
            p-id="15386"
          ></path>
        </svg>
        <span>Corn BI</span>
      </RouterLink>

      <nav class="header-actions">
        <button type="button" class="button darker" @click="goToAuth('login')">
          Login
        </button>
        <button type="button" class="button" @click="goToAuth('register')">
          Register
        </button>
      </nav>
    </header>

    <main class="welcome-hero">
      <div class="hero-copy">
        <h1>See the story<br /><span>behind your data.</span></h1>
        <p>
          Explore your data, discover meaningful patterns, and turn complex
          information into decisions with confidence.
        </p>
      </div>

      <div class="hero-visual" aria-hidden="true">
        <img src="@/assets/hero.png" alt="" />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";

type AuthMode = "login" | "register";

const router = useRouter();

function goToAuth(mode: AuthMode) {
  router.push({ name: "auth", query: { mode } });
}
</script>

<style scoped>
.welcome {
  position: relative;
  min-height: 100vh;
  padding: var(--padding-bigger);

  display: flex;
  flex-direction: column;
  overflow: hidden;
  background:
    radial-gradient(
      circle at 78% 42%,
      color-mix(in srgb, #f5b82e 12%, transparent),
      transparent 28rem
    ),
    radial-gradient(
      circle at 68% 70%,
      color-mix(in srgb, #84bd19 9%, transparent),
      transparent 24rem
    ),
    var(--bg);
}

.welcome-heading {
  position: relative;
  z-index: 1;

  display: flex;
  align-items: center;
  justify-content: space-between;
}
.header-brand {
  display: inline-flex;
  align-items: center;
  gap: var(--gap);

  color: var(--clr);
  font-size: 1.05rem;
  font-weight: 700;
  text-decoration: none;
  letter-spacing: -0.02em;
}
.header-actions {
  display: flex;
  align-items: center;
  gap: var(--gap);
}

.welcome-hero {
  width: min(1120px, 100%);
  margin: 0 auto;

  flex: 1;
  display: grid;
  grid-template-columns: minmax(0, 0.95fr) minmax(360px, 1.05fr);
  align-items: center;
  gap: clamp(32px, 7vw, 120px);
}
.hero-copy {
  max-width: 570px;
}
.hero-copy h1 {
  margin: 0;
  font-size: clamp(3.3rem, 6vw, 6.4rem);
  font-weight: 700;
  line-height: 0.98;
  letter-spacing: -0.075em;
}
.hero-copy h1 span {
  color: color-mix(in srgb, var(--clr) 48%, transparent);
}
.hero-copy p {
  max-width: 440px;
  margin: 28px 0 32px;
  color: color-mix(in srgb, var(--clr) 64%, transparent);
  font-size: 1rem;
  line-height: 1.7;
}

.hero-visual {
  position: relative;
  min-height: 460px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-visual img {
  position: relative;
  z-index: 1;
  width: min(82%, 370px);
  filter: drop-shadow(0 28px 24px color-mix(in srgb, #6c7f1d 27%, transparent));
  animation: float 5s ease-in-out infinite;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-12px);
  }
}

@media (max-width: 760px) {
  .welcome {
    padding: 20px 20px 18px;
  }

  .welcome-hero {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 64px 0 30px;
  }

  .hero-copy {
    text-align: center;
  }

  .hero-visual {
    min-height: 330px;
  }

  .hero-image {
    width: min(72%, 290px);
  }
}
</style>
