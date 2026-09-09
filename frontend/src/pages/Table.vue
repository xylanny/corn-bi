<template>
  <section class="table">
    <header class="table-toolbar">
      <div class="toolbar-info">
        <p v-if="table.fileName">{{ table.fileName }}</p>
        <p v-else>Upload a CSV or Excel file</p>
      </div>

      <div class="toolbar-actions">
        <input
          ref="fileInput"
          class="file-input"
          type="file"
          accept=".csv,.xlsx,.xls"
          @change="handleFileInput"
        />
        <button
          type="button"
          title="Upload CSV or Excel file"
          @click="openFilePicker"
        >
          <svg
            t="1788492225090"
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="23936"
            width="16"
            height="16"
            fill="var(--clr)"
          >
            <path
              d="M549.312 97.344v560c0 20.544-16.768 37.376-37.312 37.376a37.44 37.44 0 0 1-37.312-37.376v-560c0-20.48 16.768-37.312 37.312-37.312 20.48 0 37.312 16.768 37.312 37.312z"
              p-id="23937"
            ></path>
            <path
              d="M761.984 460.16L538.24 683.84a37.44 37.44 0 0 1-52.8 0 37.44 37.44 0 0 1 0-52.8l223.68-223.68a37.44 37.44 0 0 1 52.8 0 37.44 37.44 0 0 1 0 52.8z"
              p-id="23938"
            ></path>
            <path
              d="M485.568 683.776L261.952 460.16a37.44 37.44 0 0 1 0-52.8 37.44 37.44 0 0 1 52.736 0L538.368 631.04a37.44 37.44 0 0 1 0 52.8 37.44 37.44 0 0 1-52.8 0z"
              p-id="23939"
            ></path>
            <path
              d="M848 956.032H176c-61.76 0-112-50.24-112-112v-224a37.312 37.312 0 1 1 74.688 0v224c0 20.608 16.704 37.312 37.312 37.312h672a37.376 37.376 0 0 0 37.312-37.312v-224a37.312 37.312 0 1 1 74.688 0v224c0 61.76-50.24 112-112 112z"
              p-id="23940"
            ></path>
          </svg>
        </button>

        <button type="button" :disabled="isEmpty" @click="exportTable('csv')">
          <svg
            t="1788489603421"
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="11364"
            width="16"
            height="16"
            fill="var(--clr)"
          >
            <path
              d="M426.666667 53.333333h-1.450667c-58.496 0-104.405333 0-141.269333 2.986667-37.546667 3.114667-68.565333 9.429333-96.64 23.765333A245.333333 245.333333 0 0 0 80.085333 187.306667c-14.336 28.074667-20.650667 59.093333-23.722666 96.64-3.029333 36.864-3.029333 82.773333-3.029334 141.269333v173.568c0 58.496 0 104.405333 2.986667 141.269333 3.114667 37.546667 9.429333 68.565333 23.765333 96.64a245.333333 245.333333 0 0 0 107.221334 107.221334c28.074667 14.336 59.093333 20.693333 96.64 23.722666 36.864 3.029333 82.773333 3.029333 141.269333 3.029334h173.568c58.496 0 104.405333 0 141.269333-2.986667 37.546667-3.072 68.565333-9.429333 96.64-23.765333a245.333333 245.333333 0 0 0 107.221334-107.221334c14.336-28.074667 20.693333-59.093333 23.722666-96.64 3.029333-36.864 3.029333-82.773333 3.029334-141.269333V425.216c0-58.496 0-104.405333-2.986667-141.269333-3.072-37.546667-9.429333-68.565333-23.765333-96.64a245.333333 245.333333 0 0 0-107.221334-107.221334c-28.074667-14.336-59.093333-20.650667-96.64-23.722666-36.864-3.029333-82.773333-3.029333-141.269333-3.029334H426.666667zM216.32 137.088c17.578667-8.96 39.253333-14.208 72.832-16.938667 33.962667-2.773333 77.226667-2.816 137.514667-2.816h170.666666c60.245333 0 103.552 0 137.514667 2.816 33.578667 2.730667 55.253333 8.021333 72.832 16.938667a181.333333 181.333333 0 0 1 79.232 79.274667c8.96 17.536 14.208 39.210667 16.938667 72.789333 2.773333 33.962667 2.816 77.226667 2.816 137.514667v170.666666c0 60.245333 0 103.552-2.816 137.514667-2.730667 33.578667-8.021333 55.253333-16.938667 72.832a181.376 181.376 0 0 1-79.232 79.232c-17.578667 8.96-39.253333 14.208-72.832 16.938667-33.962667 2.773333-77.226667 2.816-137.514667 2.816h-170.666666c-60.288 0-103.552 0-137.514667-2.816-33.578667-2.730667-55.253333-8.021333-72.789333-16.938667a181.333333 181.333333 0 0 1-79.274667-79.232c-8.96-17.578667-14.208-39.253333-16.938667-72.832-2.773333-33.962667-2.816-77.226667-2.816-137.514667v-170.666666c0-60.288 0-103.552 2.816-137.514667 2.730667-33.578667 8.021333-55.253333 16.938667-72.789333a181.333333 181.333333 0 0 1 79.274667-79.274667z m275.626667 226.858667a32 32 0 0 0-45.226667-45.226667l-170.666667 170.666667a32 32 0 0 0 0 45.226666l170.666667 170.666667a32 32 0 1 0 45.226667-45.226667l-116.053334-116.053333H725.333333a32 32 0 0 0 0-64H375.893333l116.053334-116.053333z"
              p-id="11365"
            ></path>
          </svg>
        </button>
        <button type="button" :disabled="isEmpty" @click="exportTable('xlsx')">
          <svg
            t="1788489478935"
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="8558"
            width="16"
            height="16"
            fill="var(--clr)"
          >
            <path
              d="M426.666667 53.333333h-1.450667c-58.496 0-104.405333 0-141.269333 2.986667-37.546667 3.114667-68.565333 9.429333-96.64 23.765333A245.333333 245.333333 0 0 0 80.085333 187.306667c-14.336 28.074667-20.650667 59.093333-23.722666 96.64-3.029333 36.864-3.029333 82.773333-3.029334 141.269333v173.568c0 58.496 0 104.405333 2.986667 141.269333 3.114667 37.546667 9.429333 68.565333 23.765333 96.64a245.333333 245.333333 0 0 0 107.221334 107.221334c28.074667 14.336 59.093333 20.693333 96.64 23.722666 36.864 3.029333 82.773333 3.029333 141.269333 3.029334h173.568c58.496 0 104.405333 0 141.269333-2.986667 37.546667-3.072 68.565333-9.429333 96.64-23.765333a245.333333 245.333333 0 0 0 107.221334-107.221334c14.336-28.074667 20.693333-59.093333 23.722666-96.64 3.029333-36.864 3.029333-82.773333 3.029334-141.269333V425.216c0-58.496 0-104.405333-2.986667-141.269333-3.072-37.546667-9.429333-68.565333-23.765333-96.64a245.333333 245.333333 0 0 0-107.221334-107.221334c-28.074667-14.336-59.093333-20.650667-96.64-23.722666-36.864-3.029333-82.773333-3.029333-141.269333-3.029334H426.666667zM216.32 137.088c17.578667-8.96 39.253333-14.208 72.832-16.938667 33.962667-2.773333 77.226667-2.816 137.514667-2.816h170.666666c60.245333 0 103.552 0 137.514667 2.816 33.578667 2.730667 55.253333 8.021333 72.832 16.938667a181.333333 181.333333 0 0 1 79.232 79.274667c8.96 17.536 14.208 39.210667 16.938667 72.789333 2.773333 33.962667 2.816 77.226667 2.816 137.514667v170.666666c0 60.245333 0 103.552-2.816 137.514667-2.730667 33.578667-8.021333 55.253333-16.938667 72.832a181.376 181.376 0 0 1-79.232 79.232c-17.578667 8.96-39.253333 14.208-72.832 16.938667-33.962667 2.773333-77.226667 2.816-137.514667 2.816h-170.666666c-60.288 0-103.552 0-137.514667-2.816-33.578667-2.730667-55.253333-8.021333-72.789333-16.938667a181.333333 181.333333 0 0 1-79.274667-79.232c-8.96-17.578667-14.208-39.253333-16.938667-72.832-2.773333-33.962667-2.816-77.226667-2.816-137.514667v-170.666666c0-60.288 0-103.552 2.816-137.514667 2.730667-33.578667 8.021333-55.253333 16.938667-72.789333a181.333333 181.333333 0 0 1 79.274667-79.274667z m360.96 181.632a32 32 0 1 0-45.226667 45.226667l116.053334 116.053333H298.666667a32 32 0 1 0 0 64h349.44l-116.053334 116.053333a32 32 0 0 0 45.226667 45.226667l170.666667-170.666667a32 32 0 0 0 0-45.226666l-170.666667-170.666667z"
              p-id="8559"
            ></path>
          </svg>
        </button>
        <button type="button" :disabled="isEmpty" @click="addRow()">
          <svg
            t="1788489317484"
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="5879"
            width="16"
            height="16"
            fill="var(--clr)"
          >
            <path
              d="M511.5 957.9C264.9 957.9 65 758.2 65 511.9s199.9-446 446.5-446S958 265.6 958 511.9c0.1 246.3-199.8 446-446.5 446zM509 149.1c-200.4 0-355.8 162.2-355.8 362.3 0 200.1 155.4 356.8 355.8 356.8s362.9-156.7 362.9-356.8c0-200.1-162.5-362.3-362.9-362.3zM690.5 556h-134v133.8c0 24.6-20 44.6-44.6 44.6h-0.1c-24.6 0-44.6-19.9-44.6-44.6V556h-134c-24.7 0-44.6-19.9-44.6-44.5v-0.1c0-24.6 20-44.6 44.6-44.6h134V333c0-24.6 20-44.6 44.6-44.6h0.1c24.7 0 44.6 19.9 44.6 44.6v133.8h134c24.7 0 44.6 19.9 44.6 44.6v0.1c0 24.6-19.9 44.5-44.6 44.5z m0 0"
              p-id="5880"
            ></path>
          </svg>
        </button>
        <button
          v-if="!isEmpty"
          type="button"
          class="danger"
          @click="clearTable"
        >
          <svg
            t="1788489060694"
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="4834"
            width="16"
            height="16"
            fill="oklch(80.8% 0.114 19.571)"
          >
            <path
              d="M922.7 250.7H736v-37.3C736 130.9 669.1 64 586.7 64H437.3C354.9 64 288 130.9 288 213.3v37.3H101.3C80.7 250.7 64 267.4 64 288c0 20.6 16.7 37.3 37.3 37.3H176v485.3c0 82.5 66.9 149.3 149.3 149.3h373.3c82.5 0 149.3-66.9 149.3-149.3V325.3h74.7c20.6 0 37.3-16.7 37.3-37.3 0.1-20.6-16.6-37.3-37.2-37.3z m-560-37.4c0-41.2 33.4-74.7 74.7-74.7h149.3c41.2 0 74.7 33.4 74.7 74.7v37.3H362.7v-37.3z m410.6 597.4c0 41.2-33.4 74.7-74.7 74.7H325.3c-41.2 0-74.7-33.4-74.7-74.7V325.3h522.7v485.4z"
              p-id="4835"
            ></path>
            <path
              d="M624 433.1c-20.6 0-37.3 16.7-37.3 37.3v261.3c0 20.6 16.7 37.3 37.3 37.3 20.6 0 37.3-16.7 37.3-37.3V470.4c0-20.6-16.7-37.3-37.3-37.3zM400 433.1c-20.6 0-37.3 16.7-37.3 37.3v261.3c0 20.6 16.7 37.3 37.3 37.3 20.6 0 37.3-16.7 37.3-37.3V470.4c0-20.6-16.7-37.3-37.3-37.3z"
              fill="oklch(80.8% 0.114 19.571)"
              p-id="4836"
            ></path>
          </svg>
        </button>
      </div>
    </header>

    <div class="table-message" v-if="loading">
      <div v-if="loading">Loading file ...</div>
    </div>
    <div class="table-message error" v-else-if="!loading && error">
      {{ error }}
      <button type="button" @click="resetError">Dismiss</button>
    </div>
    <div class="table-message" v-else-if="!loading && isEmpty">no data</div>

    <div
      v-else
      class="table-show"
      :ref="setContainerRef"
      @scroll="handleScroll"
    >
      <table :style="{ width: `${tableWidth}px` }">
        <colgroup>
          <!-- 虚拟列表索引列 -->
          <col />
          <col v-for="h in table.headers" :key="`col-${h}`" />
        </colgroup>
        <thead :style="{ width: `${tableWidth}px` }">
          <tr>
            <th>#</th>
            <th v-for="h in table.headers" :key="h">
              <div>{{ h }}</div>
            </th>
          </tr>
        </thead>

        <tbody
          :style="{
            height: `${contentH}px`,
            width: `${tableWidth}px`,
          }"
        >
          <tr
            v-for="(row, idx) in vRows"
            :key="sIdx + idx"
            :style="{
              transform: `translateY(${offsetY + idx * unitH}px)`,
              width: `${tableWidth}px`,
            }"
          >
            <td>{{ sIdx + idx + 1 }}</td>
            <td v-for="header in table.headers" :key="header">
              <div>
                <input
                  :value="row[header] ?? ''"
                  @change="
                    updateRow(
                      sIdx + idx,
                      header,
                      ($event.target as HTMLInputElement).value,
                    )
                  "
                />
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup lang="ts">
import Lenis from "lenis";
import { storeToRefs } from "pinia";
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from "vue";
import { useTableStore } from "@/stores/table";
import { useVirtualList } from "@/hooks/useVirtualList";

const tableStore = useTableStore();
const { table, rows, isEmpty, loading, error } = storeToRefs(tableStore);
const {
  parseFileToTable,
  addRow,
  updateRow,
  exportTableToFile,
  isValidFileType,
  resetError,
  clearTable,
} = tableStore;

// 虚拟列表配置
const unitH = 42; // 每行固定高度（与CSS保持一致）
const visibleH = 600; // 可视容器高度
const bufferN = 6; // 缓冲区行数
const indexColumnW = 54;
const dataColumnW = 160;
const visibleRef = ref<HTMLElement | null>(null);
const fileInput = ref<HTMLInputElement | null>(null);
const lenis = ref<Lenis | null>(null);
let animationFrame = 0;

// 使用虚拟列表 hook
const {
  contentH,
  sIdx,
  offsetY,
  getVisibleData,
  setScrollTop,
  resetScrollTop,
  scrollTo,
} = useVirtualList({
  unitH,
  total: rows,
  visibleH,
  bufferN,
});

// 计算可见行数据
const vRows = computed(() => getVisibleData(table.value.rows));
const tableWidth = computed(
  () => indexColumnW + table.value.headers.length * dataColumnW,
);

// 设置容器引用（同时用于 Lenis）
function setContainerRef(
  element: Element | import("vue").ComponentPublicInstance | null,
) {
  visibleRef.value = element instanceof HTMLElement ? element : null;
}

function handleScroll(event: Event) {
  const target = event.target as HTMLElement;
  setScrollTop(target.scrollTop);
}

function openFilePicker() {
  fileInput.value?.click();
}

async function handleFileInput(event: Event) {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0] ?? null;

  try {
    await handleFileChange(file);
  } finally {
    input.value = "";
  }
}

function destroySmoothScroll() {
  if (lenis.value) {
    lenis.value.destroy();
    lenis.value = null;
  }
}

function animate(time: number) {
  lenis.value?.raf(time);
  animationFrame = requestAnimationFrame(animate);
}

async function initializeSmoothScroll() {
  await nextTick();
  const wrapper = visibleRef.value;
  const content = wrapper?.firstElementChild;
  if (!wrapper || !(content instanceof HTMLElement)) return;

  destroySmoothScroll();
  lenis.value = new Lenis({
    wrapper,
    content,
    autoRaf: false,
  });
  lenis.value.on("scroll", ({ scroll }) => {
    setScrollTop(scroll);
  });
}

watch(isEmpty, async (empty) => {
  if (empty) {
    destroySmoothScroll();
    resetScrollTop();
    return;
  }
  await initializeSmoothScroll();
});

async function handleFileChange(value: File | File[] | null) {
  const file = Array.isArray(value) ? value[0] : value;
  if (!file) return;

  resetError();
  if (!isValidFileType(file)) {
    tableStore.error = "Only CSV, XLSX, and XLS files are supported";
    return;
  }

  try {
    await parseFileToTable(file);
    // 文件加载完成后重置滚动到顶部
    resetScrollTop();
  } catch (parseError: unknown) {
    if (!error.value) {
      error.value =
        parseError instanceof Error
          ? parseError.message
          : "Unable to parse the selected file";
    }
  }
}

function exportTable(fileType: "xlsx" | "csv") {
  try {
    exportTableToFile(table.value, fileType);
  } catch (exportError: unknown) {
    if (!error.value) {
      error.value =
        exportError instanceof Error
          ? exportError.message
          : "Unable to export the table";
    }
  }
}

defineExpose({
  scrollTo,
  resetScrollTop,
});

onMounted(() => {
  animationFrame = requestAnimationFrame(animate);
  if (!isEmpty.value) void initializeSmoothScroll();
});

onUnmounted(() => {
  cancelAnimationFrame(animationFrame);
  destroySmoothScroll();
});
</script>

<style scoped>
.table {
  height: 100%;
  width: 100%;
  max-width: 100%;
  min-width: 0;
  min-height: 0;
  padding: var(--padding);

  display: flex;
  flex-direction: column;
  gap: var(--ft-sz);

  overflow: hidden;
  background: var(--bg);
}

.table-toolbar {
  min-width: 0;
  padding: var(--padding);

  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--ft-sz);
  flex-wrap: wrap;
}
.toolbar-info {
  color: gray;
  font-size: var(--clr);
  font-style: italic;
}
.toolbar-actions {
  min-width: 0;
  max-width: 100%;

  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.file-input {
  display: none;
}
.toolbar-actions :deep(input[type="file"]) {
  font-size: 12px;
}
.toolbar-actions > button {
  padding: 4px;
  border: var(--border);

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: var(--radius);
  background: var(--bg-lighter);
  cursor: pointer;
  white-space: nowrap;
  font-size: 14px;
  transition: background 0.2s;
}
.toolbar-actions > button:hover:not(:disabled) {
  background: var(--bg-darker);
}
.toolbar-actions > button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.table-message {
  padding: 10px 12px;
  border: var(--border);

  flex-shrink: 0;

  color: var(--clr);
  border-radius: var(--radius);
  background: var(--bg-lighter);
}

.table-show {
  width: 100%;
  max-width: 100%;
  min-height: 0;
  min-width: 0;
  border: var(--border);

  flex: 1;

  overflow: auto;
  scrollbar-gutter: stable;
  border-radius: var(--radius);
  color: var(--clr);
  background: var(--bg-lighter);
}

.table-show table {
  width: max-content;
  min-width: 100%;
  max-width: none;
  table-layout: fixed;
  border-collapse: separate;
  border-spacing: 0;
}

.table-show thead {
  display: table;
  width: max-content;
  table-layout: fixed;
}

.table-show tbody {
  display: block;
  position: relative;
  width: max-content;
  overflow: hidden;
}

.table-show tbody tr {
  display: table;
  width: max-content;
  table-layout: fixed;
}

/* 第1个 col：索引列，固定 54px */
.table-show colgroup col:nth-child(1) {
  width: 54px;
  min-width: 54px;
  max-width: 54px;
}

/* 第2个 col及以后：数据列，自适应宽度 */
.table-show colgroup col:nth-child(n + 2) {
  width: 160px;
  min-width: 160px;
  max-width: 160px;
}

.table-show thead > tr > th {
  position: sticky;
  top: 0;
  z-index: 2;
  width: 160px;
  min-width: 160px;
  max-width: 160px;
  height: 42px;
  padding: 0 8px;
  border-right: var(--border);
  border-bottom: var(--border);
  text-align: left;
  vertical-align: middle;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  background: var(--bg-darkest);
  font-size: 13px;
  font-weight: 600;
  box-sizing: border-box;
  line-height: 42px;
}

/* 索引列表头 - 居中 */
.table-show thead > tr > th:nth-child(1) {
  width: 54px;
  min-width: 54px;
  max-width: 54px;
  padding: 0 10px;
  text-align: center;
}

.table-show tbody > tr > td {
  width: 160px;
  min-width: 160px;
  max-width: 160px;
  height: 42px;
  padding: 0 8px;
  border-right: var(--border);
  border-bottom: var(--border);
  text-align: left;
  vertical-align: middle;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  box-sizing: border-box;
  line-height: 42px;
}

/* 索引列单元格 - 居中 */
.table-show tbody > tr > td:nth-child(1) {
  width: 54px;
  min-width: 54px;
  max-width: 54px;
  padding: 0 10px;
  text-align: center;
}

.table-show tbody > tr {
  position: absolute;
  left: 0;
  top: 0;
  width: max-content;
  height: 42px;
  background: var(--bg-lighter);
  transition: background 0.15s;
  display: table;
  table-layout: fixed;
}

.table-show tbody > tr:hover {
  background: var(--bg-lightest);
}

.table-show tbody > tr > td input {
  width: 100%;
  height: 28px;
  padding: 2px 6px;
  border: 1px solid transparent;
  border-radius: 4px;
  outline: none;
  background: transparent;
  font-size: 13px;
  box-sizing: border-box;
  line-height: 24px;
  transition:
    border-color 0.2s,
    background 0.2s;
}

.table-show tbody > tr > td input:focus {
  border-color: var(--clr);
  background: white;
}

.table-show tbody > tr > td input:hover {
  border-color: rgba(0, 0, 0, 0.15);
}

/* 滚动条相关 */
.table-show::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}
.table-show::-webkit-scrollbar-track {
  background: var(--bg-lighter);
  border-radius: 4px;
}
.table-show::-webkit-scrollbar-thumb {
  background: var(--bg-darkest);
  border-radius: 4px;
}
.table-show::-webkit-scrollbar-thumb:hover {
  background: #888;
}

.danger {
  border: 1px solid oklch(80.8% 0.114 19.571);
}

@media (max-width: 768px) {
  .table-show colgroup col:nth-child(n + 2) {
    min-width: 60px;
    max-width: 150px;
  }

  .table-show thead > tr > th,
  .table-show tbody > tr > td {
    padding: 0 4px;
    font-size: 12px;
  }

  .table-show tbody > tr > td input {
    font-size: 12px;
    height: 24px;
    padding: 1px 4px;
  }
}
</style>
