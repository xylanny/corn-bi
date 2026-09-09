<template>
  <section class="analysis">
    <div class="analysis-heading">
      <div
        v-for="(el, idx) in ['dataset', 'chart', 'analysis']"
        :key="el"
        class="heading-part"
        :class="{ active: step === idx + 1, done: step > idx + 1 }"
      >
        <span class="part-number">{{ idx + 1 }}</span>
        <small>{{ el }}</small>
      </div>
    </div>

    <div class="analysis-step">
      <div class="step-part" v-if="step === 1">
        <div class="step-content">
          <!-- 消息区域 -->
          <div class="step-messages">
            <p v-if="table.rows.length" class="message-info">
              Dataset: {{ table.fileName || "Current table" }} ({{
                table.rows.length
              }}
              rows)
            </p>
            <p v-else class="message-warning">
              No dataset loaded. Load data in the table editor first.
            </p>
          </div>

          <form class="part-form">
            <template v-if="headers.length">
              <label class="form-field">
                <span>Chart name:</span>
                <input v-model.trim="form.chartName" type="text" required />
              </label>

              <label class="form-field">
                <span>Chart type:</span>
                <select v-model="form.chartType" required>
                  <option disabled value="">Select a chart type</option>
                  <option
                    v-for="[label, value] in Object.entries(CHART_TYPE)"
                    :key="value"
                    :value="value"
                  >
                    {{ label }}
                  </option>
                </select>
              </label>
              <label class="form-field">
                <span>Start:</span>
                <input
                  v-model.number="form.start"
                  type="number"
                  min="1"
                  :max="allRows.length"
                  required
                />
              </label>
              <label class="form-field">
                <span>End:</span>
                <input
                  v-model.number="form.end"
                  type="number"
                  :min="form.start || 1"
                  :max="allRows.length"
                  required
                />
              </label>
              <label class="form-field">
                <span>X:</span>
                <select v-model="form.x" required>
                  <option disabled value="">Select a field</option>
                  <option
                    v-for="field in fieldOptions"
                    :key="field.name"
                    :value="field.name"
                    :disabled="field.name === form.y"
                  >
                    {{ field.name
                    }}{{ field.isNonNumeric ? " (non-numeric)" : "" }}
                  </option>
                </select>
              </label>
              <label class="form-field">
                <span>Y:</span>
                <select v-model="form.y" required>
                  <option disabled value="">Select a field</option>
                  <option
                    v-for="field in fieldOptions"
                    :key="field.name"
                    :value="field.name"
                    :disabled="field.name === form.x"
                  >
                    {{ field.name
                    }}{{ field.isNonNumeric ? " (non-numeric)" : "" }}
                  </option>
                </select>
              </label>
            </template>
          </form>
        </div>

        <div class="part-actions">
          <button type="button" @click="next" :disabled="!canNext">Next</button>
        </div>
      </div>

      <div class="step-part" v-else-if="step === 2">
        <div class="step-content">
          <!-- 消息区域 -->
          <div class="step-messages">
            <p v-if="chartLoading" class="message-loading">
              ⏳ Loading chart...
            </p>
            <p v-else-if="chartError" class="message-error">
              ❌ {{ chartError }}
            </p>
            <p v-else-if="chartData.length === 0" class="message-warning">
              No data available for chart
            </p>
            <p v-else class="message-success">
              Chart loaded successfully ({{ chartData.length }} data points)
            </p>
          </div>

          <div class="part-chart">
            <EChart
              v-if="!chartLoading && !chartError && chartData.length > 0"
              :name="form.chartName"
              :type="form.chartType"
              :data="chartData"
              :x="form.x"
              :y="form.y"
            />
          </div>
        </div>

        <div class="part-actions">
          <button type="button" @click="back">Back</button>
          <button
            type="submit"
            @click="next"
            :disabled="!!chartError || chartLoading || chartData.length === 0"
          >
            Next
          </button>
        </div>
      </div>

      <div class="step-part" v-else>
        <div class="step-content">
          <!-- 消息区域 -->
          <div class="step-messages">
            <p v-if="loading" class="message-loading">⏳ Analyzing chart...</p>
            <p v-else-if="message" class="message-error">{{ message }}</p>
            <p v-else-if="conclusion" class="message-success">
              Analysis completed
            </p>
            <p v-else class="message-info">💡 Enter your analysis goal below</p>
          </div>

          <form class="part-form">
            <label v-if="!conclusion" class="form-field">
              <span>Chart goal:</span>
              <textarea
                v-model.trim="form.chartGoal"
                rows="4"
                placeholder="Describe what you want to learn from this chart"
                required
              ></textarea>
            </label>
            <div v-else class="form-result">
              <div class="result-section">
                <h4>Process</h4>
                <div class="result-content" v-if="conclusion.process">
                  {{ conclusion.process }}
                </div>
              </div>
              <div class="result-section">
                <h4>Conclusion</h4>
                <div class="result-content" v-if="conclusion.chartConclusion">
                  {{ conclusion.chartConclusion }}
                </div>
              </div>
            </div>
          </form>
        </div>

        <div class="part-actions">
          <button type="button" @click="step--">Back</button>
          <button
            v-if="!conclusion"
            type="button"
            :disabled="loading || !form.chartGoal"
            @click="analysis"
          >
            {{ loading ? "Analyzing..." : "Analyze" }}
          </button>
          <button v-else type="button" @click="clearAnalysis">Clear</button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { CHART_TYPE } from "@/types/constants";
import { useTableStore } from "@/stores/table";
import { useChartStore } from "@/stores/chart";
import { storeToRefs } from "pinia";
import EChart from "@/components/EChart.vue";
import { isNonNumeric } from "@/utils/judger";

const tableStore = useTableStore();
const { table } = storeToRefs(tableStore);
const chartStore = useChartStore();
const { loading, conclusion, message } = storeToRefs(chartStore);
const headers = computed(() => table.value.headers);
const allRows = computed(() => table.value.rows);
const selectedRows = computed(() => {
  const start = Math.max(1, form.value.start);
  const end = Math.min(allRows.value.length, form.value.end);
  return start <= end ? allRows.value.slice(start - 1, end) : [];
});
const fieldOptions = computed(() =>
  headers.value.map((name) => ({
    name,
    isNonNumeric: allRows.value.some((row) => isNonNumeric(row[name])),
  })),
);
const chartData = computed(() =>
  selectedRows.value.map((row) => ({
    [form.value.x]: row[form.value.x] ?? "",
    [form.value.y]: row[form.value.y] ?? "",
  })),
);

const step = ref(1);
const chartLoading = ref(false);
const chartError = ref("");
const form = ref({
  chartName: "",
  chartType: "",
  chartGoal: "",
  x: "",
  y: "",
  start: 0,
  end: table.value.rows.length,
});
const canNext = computed(() =>
  Boolean(
    form.value.chartName &&
    form.value.chartType &&
    form.value.x &&
    form.value.y,
  ),
);

function next() {
  step.value++;
  if (step.value === 2) {
    refreshChart();
  }
}

function back() {
  step.value--;
}

function refreshChart() {
  chartError.value = "";
  chartLoading.value = true;
  // 模拟加载
  setTimeout(() => {
    chartLoading.value = false;
  }, 500);
}

async function analysis() {
  if (!table.value.rows.length || !selectedRows.value.length) {
    return;
  }

  const selectedTable = {
    headers: [form.value.x, form.value.y],
    rows: selectedRows.value.map((row) => ({
      [form.value.x]: row[form.value.x] ?? "",
      [form.value.y]: row[form.value.y] ?? "",
    })),
    fileName: table.value.fileName,
  };

  await chartStore.generate({
    chartName: form.value.chartName,
    chartGoal: form.value.chartGoal,
    chartType: form.value.chartType,
    file: tableStore.exportTableToFile(selectedTable, "csv", false),
    xField: form.value.x,
    yField: form.value.y,
  });
}

function clearAnalysis() {
  chartStore.clear();
  form.value.chartGoal = "";
}
</script>

<style scoped lang="css">
.analysis {
  width: 100%;
  height: 100%;
  padding: var(--padding);

  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: var(--padding);
}

.analysis-heading {
  display: flex;
  justify-content: space-evenly;
  flex-shrink: 0;
  padding: var(--padding-smaller) 0;
}

.heading-part {
  display: flex;
  align-items: center;
  gap: 6px;
  color: gray;
  font-size: var(--ft-sz);
}

.heading-part.active,
.heading-part.done {
  color: var(--clr);
}

.part-number {
  width: 32px;
  height: 32px;
  display: grid;
  place-items: center;
  border: var(--border);
  border-radius: 50%;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.heading-part.active .part-number,
.heading-part.done .part-number {
  background: var(--clr);
  color: var(--bg);
}

.analysis-step {
  min-width: 0;
  min-height: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.step-part {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: var(--padding);
}

.step-content {
  min-height: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--padding-smaller);
  overflow: hidden;
}

/* ===== 消息区域 ===== */
.step-messages {
  flex-shrink: 0;
  padding: var(--padding-smaller);
  border-radius: var(--radius);
  background: var(--bg-darker);
}

.step-messages p {
  margin: 0;
  font-size: var(--ft-sz);
  line-height: 1.5;
}

.message-info {
  color: var(--clr);
}

.message-success {
  color: #52c41a;
}

.message-warning {
  color: #faad14;
}

.message-error {
  color: #ff4d4f;
}

.message-loading {
  color: #1890ff;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* ===== 表单 ===== */
.part-form {
  min-height: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--padding-smaller);
  overflow: auto;
  padding: var(--padding-smaller);
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.form-field span {
  font-size: 13px;
  font-weight: 500;
  color: color-mix(in srgb, var(--clr) 80%, transparent);
}

.form-field input,
.form-field select,
.form-field textarea {
  width: 100%;
  border: var(--border);
  border-radius: var(--radius);
  padding: var(--padding-smallest);
  font-size: var(--ft-sz);
  outline: none;
  background: var(--bg-lightest);
  color: var(--clr);
  transition: border-color 0.2s;
}

.form-field input:focus,
.form-field select:focus,
.form-field textarea:focus {
  border-color: var(--clr);
}

.form-field textarea {
  min-height: 100px;
  resize: vertical;
  font-family: inherit;
}

/* ===== 图表区域 ===== */
.part-chart {
  width: 100%;
  min-height: 0;
  flex: 1;
  padding: var(--padding-smaller);
  border: var(--border);
  border-radius: var(--radius);
  background: var(--bg-lighter);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.part-chart :deep(.chart-container) {
  width: 100%;
  height: 100%;
}

/* ===== 结果区域 ===== */
.form-result {
  min-width: 0;
  min-height: 0;
  flex: 1;
  padding: var(--padding);
  overflow: auto;
  border: var(--border);
  border-radius: var(--radius);
  background: var(--bg-lighter);
  display: flex;
  flex-direction: column;
  gap: var(--padding);
}

.result-section {
  display: flex;
  flex-direction: column;
  gap: var(--padding-smaller);
}

.result-section h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--clr);
}

.result-content {
  color: var(--clr);
  white-space: pre-wrap;
  overflow-wrap: anywhere;
  line-height: 1.8;
  padding: var(--padding-smaller);
  border-radius: var(--radius);
  background: var(--bg-darker);
  font-size: 14px;
}

/* ===== 操作按钮 ===== */
.part-actions {
  flex-shrink: 0;
  display: flex;
  justify-content: flex-end;
  gap: var(--gap-smaller);
  padding-top: var(--padding-smaller);
  border-top: var(--border);
}

.part-actions > button {
  border: var(--border);
  padding: var(--padding-smaller);
  border-radius: var(--radius);
  font-size: var(--ft-sz);
  cursor: pointer;
  background: var(--bg-darker);
  color: var(--clr);
  transition: all 0.2s;
  min-width: 80px;
}

.part-actions > button:hover:not(:disabled) {
  background: var(--bg-darkest);
  transform: translateY(-1px);
}

.part-actions > button:disabled {
  color: color-mix(in srgb, var(--clr) 45%, transparent);
  background: var(--bg-darker);
  cursor: not-allowed;
  opacity: 0.6;
  transform: none;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .analysis-heading {
    font-size: 12px;
  }

  .part-number {
    width: 28px;
    height: 28px;
    font-size: 12px;
  }

  .form-field textarea {
    min-height: 60px;
  }

  .part-actions > button {
    min-width: 60px;
    padding: var(--padding-smallest);
    font-size: 12px;
  }
}
</style>
