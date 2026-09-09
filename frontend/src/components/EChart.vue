<template>
  <div ref="chartElement" class="chart"></div>
</template>

<script setup lang="ts">
import * as echarts from "echarts";
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from "vue";

const MAX_RENDER_POINTS = 2000;
const MAX_CATEGORY_POINTS = 50;

type Point = { name: string; value: number };
export type Row = Record<string, string | number | boolean | null>;
export interface ChartProps {
  data: Row[];
  name: string;
  type: string;
  x: string;
  y: string;
}

const props = defineProps<ChartProps>();
const emit = defineEmits<{
  (event: "error", error: Error): void;
}>();

const chartElement = ref<HTMLElement | null>(null);

function renderChart() {
  if (!chartElement.value) {
    return;
  }

  const chart = echarts.getInstanceByDom(chartElement.value);
  chart?.dispose();
  if (!props.data.length || !props.x || !props.y) {
    return;
  }

  try {
    const points = normalizePoints(props.data);
    const series = getSeries(points);
    const instance = echarts.init(chartElement.value);
    const isCircularChart = ["pie", "funnel", "gauge", "radar"].includes(
      props.type,
    );
    const hasLargeDataset = props.data.length > MAX_RENDER_POINTS;

    instance.setOption({
      animation: !hasLargeDataset,
      tooltip: {
        trigger: isCircularChart ? "item" : "axis",
        valueFormatter: (value: number) => formatValue(value),
      },
      grid: { left: 48, right: 24, top: 32, bottom: 48, containLabel: true },
      title: props.name ? { text: props.name } : undefined,
      xAxis: isCircularChart
        ? undefined
        : {
            type: "category",
            data: points.map((point) => point.name),
            axisLabel: {
              interval: Math.max(0, Math.ceil(points.length / 12) - 1),
              hideOverlap: true,
            },
          },
      yAxis: isCircularChart
        ? undefined
        : {
            type: "value",
            axisLabel: { formatter: (value: number) => formatValue(value) },
          },
      dataZoom:
        !isCircularChart && hasLargeDataset
          ? [
              { type: "inside", start: 0, end: 100 },
              { type: "slider", height: 18, bottom: 8 },
            ]
          : undefined,
      series,
    });
  } catch (error) {
    emit(
      "error",
      error instanceof Error ? error : new Error("Unable to render chart"),
    );
  }
}

function normalizePoints(rows: Row[]): Point[] {
  const points = rows.map((row) => ({
    name: String(row[props.x] ?? ""),
    value: toNumber(row[props.y]),
  }));

  if (["pie", "funnel"].includes(props.type)) {
    const totals = new Map<string, number>();
    for (const point of points) {
      totals.set(point.name, (totals.get(point.name) ?? 0) + point.value);
    }
    return [...totals.entries()]
      .map(([name, value]) => ({ name, value }))
      .sort((a, b) => Math.abs(b.value) - Math.abs(a.value))
      .slice(0, MAX_CATEGORY_POINTS);
  }

  if (props.type === "radar") {
    return points.slice(0, MAX_CATEGORY_POINTS);
  }

  if (points.length <= MAX_RENDER_POINTS) {
    return points;
  }

  const bucketSize = Math.ceil(points.length / MAX_RENDER_POINTS);
  return Array.from(
    { length: Math.ceil(points.length / bucketSize) },
    (_, index) => {
      const bucket = points.slice(index * bucketSize, (index + 1) * bucketSize);
      return {
        name: bucket[0]?.name ?? "",
        value:
          bucket.reduce((sum, point) => sum + point.value, 0) / bucket.length,
      };
    },
  );
}

function toNumber(value: Row[string]) {
  const number = typeof value === "number" ? value : Number(value);
  return Number.isFinite(number) ? number : 0;
}

function formatValue(value: number) {
  const absolute = Math.abs(value);
  if (absolute >= 100_000_000) {
    return `${(value / 100_000_000).toFixed(2)}亿`;
  }
  if (absolute >= 10_000) {
    return `${(value / 10_000).toFixed(2)}万`;
  }
  return value.toLocaleString();
}

function getSeries(points: Point[]) {
  const yValues = points.map((point) => point.value);

  if (props.type === "pie") {
    return [
      {
        type: "pie",
        radius: "65%",
        data: points,
      },
    ];
  }

  if (props.type === "funnel") {
    return [
      {
        type: "funnel",
        data: points,
      },
    ];
  }

  if (props.type === "gauge") {
    const value = yValues[yValues.length - 1] ?? 0;
    return [
      {
        type: "gauge",
        detail: { formatter: (value: number) => formatValue(value) },
        data: [{ value }],
      },
    ];
  }

  if (props.type === "radar") {
    return [
      {
        type: "radar",
        data: [{ value: yValues, name: props.y }],
      },
    ];
  }

  return [
    {
      type: props.type === "area" ? "line" : props.type,
      smooth: props.type === "line" || props.type === "area",
      areaStyle: props.type === "area" ? {} : undefined,
      data: yValues,
      large: props.data.length > MAX_RENDER_POINTS,
      largeThreshold: MAX_RENDER_POINTS,
      progressive: 500,
      progressiveThreshold: MAX_RENDER_POINTS,
    },
  ];
}

onMounted(async () => {
  await nextTick();
  renderChart();
});

watch(() => [props.data, props.x, props.y, props.type], renderChart);

onBeforeUnmount(() => {
  if (chartElement.value) {
    echarts.dispose(chartElement.value);
  }
});
</script>

<style scoped lang="css">
.chart {
  width: 100%;
  height: 100%;
}
</style>
