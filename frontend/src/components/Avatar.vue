<template>
  <div
    class="avatar"
    :style="{
      width: `${avatarSize()}px`,
      height: `${avatarSize()}px`,
      ...avatarShape(),
    }"
    @dblclick="handleDoubleClick"
  >
    <div v-if="props.src" class="avatar-img">
      <img :src="props.src" :alt="props.alt" />
    </div>

    <div v-if="!props.src" class="avatar-skeleton">
      {{ props.text }}
    </div>

    <div class="avatar-hint">
      <span>change</span>
    </div>

    <input
      ref="fileInputRef"
      class="avatar-file"
      type="file"
      accept="image/*"
      @change="handleFileChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";

export interface AvatarProps {
  src?: string;
  alt?: string;
  text?: string;
  fit?: "fill" | "contain" | "cover" | "scale-down";
  size?: number | "middle" | "small" | "large";
  shape?: "circle" | "square";
}

const props = withDefaults(defineProps<AvatarProps>(), {
  src: "",
  alt: "avatar",
  text: "?",
  fit: "cover",
  size: "middle",
  shape: "circle",
});

// 计算头像大小
const avatarSize = () => {
  if (typeof props.size === "string") {
    switch (props.size) {
      case "middle":
        return 64;
      case "small":
        return 32;
      case "large":
        return 128;
      default:
        return 64;
    }
  } else {
    return props.size;
  }
};

// 计算头像形状
const avatarShape = () => {
  if (props.shape === "circle") {
    return { borderRadius: "50%" };
  }
  return {};
};

// 文件输入引用
const fileInputRef = ref<HTMLInputElement | null>(null);

// 定义事件
const emit = defineEmits<{
  (e: "change", file: File): void;
  (e: "changeBase64", base64: string): void;
}>();

const handleDoubleClick = () => {
  fileInputRef.value?.click();
};

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  const files = target.files;

  // 若没有文件上传时
  if (!files || files.length === 0) return;

  const file = files[0];
  // 若文件不是图片格式时
  if (!file.type.startsWith("image/")) {
    alert("please Upload img file!");
    target.value = "";
    return;
  }

  // 若文件大小超过1MB时
  if (file.size > 1 * 1024 * 1024) {
    alert("please restrict size under 1MB!");
    target.value = "";
    return;
  }

  // 发布消息
  emit("change", file);
};
</script>

<style scoped lang="css">
.avatar {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--x-bg-first);
  transition: all 0.2s ease;
  cursor: pointer; /* 添加指针样式 */
  overflow: hidden;
  user-select: none; /* 防止双击选中文本 */
}

/* 悬停效果 */
.avatar:hover .avatar-hint {
  opacity: 1;
}

/* 图片容器 */
.avatar-img,
.avatar-skeleton {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-img > img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 占位符 */
.avatar-skeleton {
  color: var(--x-clr-first);
  font-size: 1.5rem;
  font-weight: 300;
  background: var(--x-bg-second);
}

.avatar-hint {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg);
  color: #fff;
  font-size: 12px;
  pointer-events: none;
  border-radius: inherit;
  opacity: 0;

  transition: opacity 0.3s ease;
}

/* 隐藏文件输入 */
.avatar-file {
  display: none;
}

.avatar-img > img {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
