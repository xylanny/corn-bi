<template>
  <div class="file-loader">
    <input
      ref="fileInput"
      type="file"
      :multiple="multiple"
      :accept="accept"
      @change="handleChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";

interface FileLoaderProps {
  modelValue?: File | File[] | null;
  multiple?: boolean;
  accept?: string;
}

const props = withDefaults(defineProps<FileLoaderProps>(), {
  modelValue: null,
  multiple: false,
  accept: undefined,
});
const emit = defineEmits<{
  (e: "update:modelValue", value: File | File[] | null): void;
}>();

const fileInput = ref<HTMLInputElement | null>(null);

function handleChange(e: Event) {
  const target = e.target as HTMLInputElement;
  const files = target.files;

  if (!files || files.length === 0) {
    emit("update:modelValue", null);
    if (fileInput.value) {
      fileInput.value.value = "";
    }
    return;
  }

  const nextValue = props.multiple ? Array.from(files) : files[0] ?? null;
  emit("update:modelValue", nextValue);

  if (fileInput.value) {
    fileInput.value.value = "";
  }
}
</script>
