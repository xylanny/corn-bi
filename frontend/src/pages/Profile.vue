<template>
  <section class="profile">
    <div class="profile-back" @click="goToBoardPage" aria-hidden="true">
      <svg
        t="1788491910020"
        class="icon"
        viewBox="0 0 1024 1024"
        version="1.1"
        xmlns="http://www.w3.org/2000/svg"
        p-id="21612"
        width="24"
        height="24"
      >
        <path
          d="M512 0C229.888 0 0 229.888 0 512s229.888 512 512 512 512-229.888 512-512S794.112 0 512 0z m0 915.968c-222.72 0-403.968-181.248-403.968-403.968S289.28 108.032 512 108.032s403.968 181.248 403.968 403.968-181.248 403.968-403.968 403.968z"
          fill="var(--ft-clr)"
          p-id="21613"
        ></path>
        <path
          d="M586.24 364.544H425.984l13.824-13.824c13.824-13.824 13.824-36.864 0-50.688s-36.864-13.824-51.2 0L308.224 379.904c-13.824 13.824-13.824 36.864 0 50.688l80.384 80.384c7.168 7.168 16.384 10.752 25.6 10.752 9.216 0 18.432-3.584 25.6-10.752 13.824-13.824 13.824-36.864 0-50.688l-3.584-3.584h150.016c37.376 0 67.584 36.864 67.584 82.432s-30.208 82.432-67.584 82.432h-230.4c-25.6 0-46.08 20.48-46.08 46.08s20.48 46.08 46.08 46.08h230.912c88.576 0 160.256-78.336 160.256-175.104s-72.192-174.08-160.768-174.08z"
          fill="var(--ft-clr)"
          p-id="21614"
        ></path>
      </svg>
    </div>

    <div class="profile-card">
      <div class="card-heading">
        <div class="heading-avatar">
          <Avatar
            :size="128"
            :src="`data:image/jpeg;base64,${user?.userAvatar}`"
            @change="changeAvatar"
          />
        </div>
      </div>

      <div class="card-details">
        <template v-for="(value, key) in userPart" :key="key">
          <div class="detail-item">
            <span class="detail-label">{{
              key.startsWith("user") ? key.slice(4) : key
            }}</span>
            <span class="detail-value">{{ value ?? "-" }}</span>
          </div>
        </template>
      </div>
    </div>

    <div class="profile-actions">
      <button type="button" @click="logoutUser">logout</button>
    </div>
  </section>
</template>

<script setup lang="ts">
import type { UserVO } from "@/api/types";
import Avatar from "@/components/Avatar.vue";
import { useUserStore } from "@/stores/user";
import { transformFileToBase64WithCompression } from "@/utils/transformer";
import { storeToRefs } from "pinia";
import { computed } from "vue";
import { useRouter } from "vue-router";

const userStore = useUserStore();
const { user } = storeToRefs(userStore);
const { logout } = userStore;
const userPart = computed(() => {
  const { token, userAvatar, ...userPart } = user.value as UserVO;
  return userPart;
});
const router = useRouter();

function goToBoardPage() {
  router.replace("/board");
}

function logoutUser() {
  logout();

  router.replace("/welcome");
}

async function changeAvatar(file: File) {
  // 文件不存在时
  if (!file) return;

  // 文件大于1MB时
  if (file.bytes.length > 1 * 1024 * 1024) {
    return;
  }
  try {
    // 转换成Base64字符串
    const base64 = await transformFileToBase64WithCompression(
      file,
      500,
      0.8,
      48,
    );
    console.log("base64:", base64);

    // 发送后端
    // await update({ userAvatar: base64 });

    // 若上传头像成功，则乐观更新
    user.value!.userAvatar = base64;
  } catch (error) {
    console.error("transform file to base64 failure,", error);
  }
}
</script>

<style scoped lang="css">
.profile {
  position: relative;
  width: 100%;
  min-height: 100vh;
  padding: 32px;

  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
  justify-content: center;

  background: var(--bg);
}

.profile-back {
  position: absolute;
  top: 32px;
  left: 32px;
  padding: 8px 12px;
  border: var(--border);

  display: inline-flex;
  justify-content: center;
  align-items: center;
  gap: 8px;

  border-radius: var(--radius);
  background: var(--bg-lighter);
  cursor: pointer;
  font-size: 14px;

  transition:
    background 0.2s,
    color 0.2s;
}
.profile-back:hover {
  background: var(--bg-darkest);
}

.profile-card {
  width: min(100%, 560px);
  padding: 24px;
  border: var(--border);

  border-radius: var(--radius-bigger);
  background: var(--bg-lighter);
}
.card-heading {
  padding-bottom: 24px;
  border-bottom: var(--border);

  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
}
.heading-avatar {
  width: 128px;
  height: 128px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border: var(--border);
  border-radius: 50%;
  background: var(--bg-darkest);
  font-size: 24px;
}

.card-details {
  display: grid;
  gap: 0;
}
.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 14px 0;
  border-bottom: var(--border);
}
.detail-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}
.detail-label {
  color: #777;
  font-size: var(--fr-sz);
}
.detail-value {
  overflow: hidden;
  color: var(--ft-clr);
  font-size: 14px;
  text-align: right;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.profile-actions {
  display: flex;
  justify-content: flex-end;
  gap: 4px;
}
.profile-actions > button {
  border: var(--border);
  padding: var(--padding-smaller);

  border-radius: var(--radius);
  font-size: var(--ft-sz);
  cursor: pointer;
  background: var(--bg-darker);

  transition: var(--transition);
}
.profile-actions > button:hover {
  background: var(--bg-darkest);
}
</style>
