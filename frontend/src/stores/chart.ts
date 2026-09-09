import { chartAPI } from "@/api/modules/chart";
import type { ChartAnalysisDTO, ChartAnalysisVO } from "@/api/types";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useChartStore = defineStore("chart", () => {
  const result = ref<ChartAnalysisVO | null>(null);
  const message = ref("");
  const loading = ref(false);

  async function generate(params: {
    chartName: string;
    chartGoal: string;
    chartType: string;
    file: File;
    xField?: string;
    yField?: string;
  }) {
    const { chartName, chartGoal, chartType, file, xField, yField } = params;

    message.value = "";
    result.value = null;

    if (!chartName || !chartGoal || !chartType || !file) {
      message.value =
        "Missing required fields: chartName, chartGoal, chartType, or file";
      return;
    }

    const formData = new FormData();
    formData.append("file", file);
    formData.append("chartGoal", chartGoal);
    formData.append("chartType", chartType);
    if (xField) formData.append("x", xField);
    if (yField) formData.append("y", yField);

    loading.value = true;
    message.value = "Analyzing chart...";

    try {
      const responseBody = await chartAPI.generateConclusionByAi(
        formData as unknown as ChartAnalysisDTO,
      );

      if (responseBody.code !== 20000 || !responseBody.data) {
        message.value = responseBody.message || "Analysis failed";
        return;
      }

      result.value = responseBody.data;
      message.value = "Analysis completed successfully!";
    } catch (error) {
      console.error("Chart analysis error:", error);
      message.value =
        error instanceof Error ? error.message : "Analysis failed";
    } finally {
      loading.value = false;
    }
  }

  function clear() {
    result.value = null;
    message.value = "";
  }

  return {
    conclusion: result,
    message,
    loading,
    generate,
    clear,
  };
});
