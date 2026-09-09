<template>
  <div class="kmeans">
    <form class="kmeans-form" v-if="!result" @submit.prevent="submit">
      <template v-if="allCols.length">
        <div class="form-field">
          <span>Columns:</span>
          <span class="column-options">
            <label
              v-for="column in allCols"
              :key="column"
              class="column-option"
            >
              <input v-model="selectedCols" type="checkbox" :value="column" />
              <span>{{ column }}</span>
            </label>
          </span>
        </div>

        <label class="form-field">
          <span>K:</span>
          <input v-model.number="form.k" type="number" min="1" required />
        </label>

        <label class="form-field">
          <span>Max iterations:</span>
          <input v-model.number="form.maxIterations" type="number" min="1" />
        </label>

        <label class="form-field">
          <span>Tolerance:</span>
          <input
            v-model.number="form.tolerance"
            type="number"
            min="0"
            step="any"
          />
        </label>

        <label class="form-field">
          <span>Seed:</span>
          <input v-model.number="form.seed" type="number" />
        </label>
      </template>

      <p v-else class="form-info">No columns found</p>

      <p class="form-info" v-if="table.rows.length">
        Dataset: {{ table.fileName }} ({{ table.rows.length }}
        rows)
      </p>
      <p v-if="error" class="form-error">{{ error }}</p>
    </form>

    <section v-else class="kmeans-result">
      <div class="result-show">
        <div class="show-heading">
          <span v-if="result.k !== undefined">K = {{ result.k }}</span>
        </div>

        <div class="show-metrics">
          <div class="result-metric">
            <span>Silhouette score</span>
            <strong>
              {{
                result.silhouetteScore !== undefined
                  ? result.silhouetteScore.toFixed(4)
                  : "-"
              }}
            </strong>
          </div>
          <div class="result-metric">
            <span>Data points</span>
            <strong>{{ result.labels?.length ?? 0 }}</strong>
          </div>
        </div>

        <div v-if="clusterSummary.length" class="cluster-chart">
          <h3>Cluster sizes</h3>
          <div
            v-for="cluster in clusterSummary"
            :key="cluster.index"
            class="cluster-row"
          >
            <span class="cluster-label">Cluster {{ cluster.index + 1 }}</span>
            <div class="cluster-track">
              <span
                class="cluster-bar"
                :style="{ width: `${cluster.percent}%` }"
              ></span>
            </div>
            <strong>{{ cluster.count }}</strong>
          </div>
        </div>

        <div v-if="result.centroids?.length" class="centroid-section">
          <h3>Centroids</h3>
          <div class="centroid-table">
            <table>
              <thead>
                <tr>
                  <th>Cluster</th>
                  <th v-for="column in selectedCols" :key="column">
                    {{ column }}
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(centroid, index) in result.centroids" :key="index">
                  <td>Cluster {{ index + 1 }}</td>
                  <td v-for="(value, valueIndex) in centroid" :key="valueIndex">
                    {{ formatNumber(value) }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>

    <div class="kmeans-actions">
      <button
        type="button"
        @click="result ? clearResult() : submit()"
        :disabled="
          !result &&
          (loading ||
            !table.rows.length ||
            !selectedCols.length ||
            selectedCols.length < 2)
        "
      >
        {{ result ? "Again" : "Analyze" }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from "vue";
import { storeToRefs } from "pinia";
import type { KMeansVO } from "@/api/types";
import { useTableStore } from "@/stores/table";

const tableStore = useTableStore();
const { table } = storeToRefs(tableStore);
const { analysisByKMeans } = tableStore;
const allCols = computed(() => table.value.headers);
const selectedCols = ref<string[]>([]);
const loading = ref(false);
const error = ref("");
const result = ref<KMeansVO | null>(null);
const clusterSummary = computed(() => {
  const counts = result.value?.nums ?? [];
  const total = counts.reduce((sum, count) => sum + count, 0); // 计算总数据点数

  return counts.map((count, index) => ({
    index,
    count,
    percent: total > 0 ? (count / total) * 100 : 0, // 占总数的百分比
  }));
});

const form = reactive({
  k: 3,
  maxIterations: 100,
  tolerance: 0.0001,
  seed: undefined as number | undefined,
});

// 若表的header部分是变化，则删除不存在的列
watch(
  () => table.value.headers,
  (headers) => {
    selectedCols.value = selectedCols.value.filter((col) =>
      headers.includes(col),
    );
  },
  { immediate: true },
);

async function submit() {
  if (!table.value.rows.length || !selectedCols.value.length) return;

  loading.value = true;
  error.value = "";
  result.value = null;

  try {
    result.value = await analysisByKMeans({
      columns: selectedCols.value,
      k: form.k,
      maxIterations: form.maxIterations,
      tolerance: form.tolerance,
      seed: form.seed as number,
    });
  } catch (err) {
    error.value =
      err instanceof Error ? err.message : "K-Means analysis failed";
  } finally {
    loading.value = false;
  }
}

function formatNumber(value: number) {
  return Number.isFinite(value) ? value.toFixed(4) : "-";
}

function clearResult() {
  result.value = null;
  error.value = "";
}
</script>

<style scoped lang="css">
.kmeans {
  width: 100%;
  height: 100%;
  padding: var(--padding);

  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.kmeans-form,
.kmeans-result {
  min-height: 0;
  flex: 1;
  padding: var(--padding);

  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow: hidden;
}
.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.form-info {
  font-style: italic;
  font-size: var(--ft-sz);
}
.form-error {
  color: oklch(65% 0.16 20);
}
.form-field input,
.form-field select {
  width: 100%;
  border: var(--border);
  border-radius: var(--radius);
  padding: var(--padding-smallest);
  font-size: var(--ft-sz);
  outline: none;
  background: var(--bg-lightest);
}
.column-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.column-option {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.column-option input {
  appearance: none;
  width: 14px;
  height: 14px;
  margin: 0;
  border: var(--border);
  border-radius: 50%;
  background: var(--bg-lightest);
  cursor: pointer;
}
.column-option input:checked {
  border-color: var(--clr);
  background: radial-gradient(circle, var(--clr) 0 4px, transparent 4.5px);
}
.columns-field {
  border: var(--border);
  border-radius: var(--radius);
  padding: var(--padding-smaller);
}
.checkbox-field {
  display: flex;
  align-items: center;
  gap: 8px;
}

.kmeans-actions {
  display: flex;
  justify-content: flex-end;
  gap: 4px;
}
.kmeans-actions > button {
  border: var(--border);
  padding: var(--padding-smaller);
  border-radius: var(--radius);
  font-size: var(--ft-sz);
  cursor: pointer;
  background: var(--bg-darker);
  transition: var(--transition);
}
.kmeans-actions > button:hover {
  background: var(--bg-darkest);
}
.kmeans-actions > button:disabled {
  color: color-mix(in srgb, var(--clr) 45%, transparent);
  background: var(--bg-darker);
  cursor: not-allowed;
  opacity: 0.6;
}

.result-show {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.show-heading,
.show-metrics,
.cluster-row {
  display: flex;
  align-items: center;
}

.show-heading {
  justify-content: space-between;
  gap: var(--padding-smaller);
  margin-bottom: var(--padding-smaller);
}

.show-heading h2,
.cluster-chart h3,
.centroid-section h3 {
  margin: 0;
}

.show-heading h2 {
  font-size: var(--ft-sz);
}

.show-heading span {
  color: color-mix(in srgb, var(--clr) 65%, transparent);
}

.show-metrics {
  gap: 8px;
  margin-bottom: var(--padding);
}

.result-metric {
  flex: 1;
  min-width: 0;
  padding: var(--padding-smaller);
  border: var(--border);
  border-radius: var(--radius);
  background: var(--bg-darker);
}

.result-metric span,
.result-metric strong {
  display: block;
}

.result-metric span {
  font-size: 13px;
}

.result-metric strong {
  margin-top: 4px;
  font-size: 20px;
}

.cluster-chart,
.centroid-section {
  margin-top: var(--padding);
}

.cluster-chart h3,
.centroid-section h3 {
  margin-bottom: 8px;
  font-size: var(--ft-sz);
}

.cluster-row {
  gap: 8px;
  margin-top: 8px;
}

.cluster-label {
  flex: 0 0 90px;
  font-size: 13px;
}

.cluster-track {
  flex: 1;
  height: 10px;
  overflow: hidden;
  border-radius: 999px;
  background: var(--bg-darker);
}

.cluster-bar {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: var(--clr);
}

.cluster-row strong {
  flex: 0 0 32px;
  text-align: right;
}

.centroid-table {
  overflow-x: auto;
  border: var(--border);
  border-radius: var(--radius);
}

.centroid-table table {
  width: 100%;
  min-width: max-content;
  border-collapse: collapse;
}

.centroid-table th,
.centroid-table td {
  padding: 8px;
  border-right: var(--border);
  border-bottom: var(--border);
  text-align: left;
  white-space: nowrap;
}

.centroid-table th {
  background: var(--bg-darker);
}
</style>
