import type { Table, TableRow } from "@/types";
// import { datasetAPI } from "@/api";
// import type { KMeanData, KMeanReq } from "@/api/types";
import { defineStore } from "pinia";
import { computed, ref } from "vue";
import * as XLSX from "xlsx";

export const useTableStore = defineStore("table", () => {
  const table = ref<Table>({ headers: [], rows: [], fileName: "" });
  const loading = ref(false);
  const error = ref<string | null>(null);

  const rows = computed(() => table.value.rows.length);
  const cols = computed(() => table.value.headers.length);
  const isEmpty = computed(() => rows.value === 0);

  function parseFileToTable(file: File): Promise<Table> {
    loading.value = true;
    error.value = null;

    return new Promise((resolve, reject) => {
      // 创建文件读取流
      const reader = new FileReader();

      reader.onload = (e) => {
        try {
          const data = e.target?.result;

          // 选择读取方式，根据文件类型（仅支持.csv、.xlsx、.xls）
          let workbook: XLSX.WorkBook;
          if (file.name.endsWith(".csv")) {
            const csvData = data as string;
            workbook = XLSX.read(csvData, {
              type: "string",
              raw: true, // 保留原始数据
              codepage: 65001, // UTF-8编码
            });
          } else {
            const arrayBuffer = data as ArrayBuffer;
            workbook = XLSX.read(arrayBuffer, {
              type: "array",
              raw: true,
              cellDates: true, // 保留日期格式
            });
          }

          // 获取第一个sheet
          const firstSheetName = workbook.SheetNames[0];
          const worksheet = workbook.Sheets[firstSheetName];

          // 转换为JSON
          const jsonData = XLSX.utils.sheet_to_json(worksheet, {
            defval: "", // 空单元格填充空字符串
            raw: false, // 转为显示格式
          }) as TableRow[];

          // 提取字段行/字段名数组
          const headers = jsonData.length > 0 ? Object.keys(jsonData[0]) : [];

          // 保存到当前存储库
          table.value = {
            headers,
            rows: jsonData,
            fileName: file.name,
          };

          // 加载完毕，改变loading状态为false
          loading.value = false;
          resolve(table.value);
        } catch (err) {
          loading.value = false;
          error.value =
            err instanceof Error ? err.message : "parse file to table failure";
          reject(new Error(`parse file to table failure, ${err}`));
        }
      };

      reader.onerror = () => {
        loading.value = false;
        error.value = "parse file to table failure";
        reject(new Error("read file failure"));
      };

      if (file.name.endsWith(".csv")) reader.readAsText(file, "UTF-8");
      else reader.readAsArrayBuffer(file);
    });
  }

  function addRow(position: "start" | "end" = "end") {
    const newRow: TableRow = {};
    table.value.headers.forEach((header) => {
      newRow[header] = "";
    });

    if (position === "start") {
      table.value.rows.unshift(newRow);
    } else {
      table.value.rows.push(newRow);
    }
  }

  function deleteRow(idx: number): void {
    if (idx < 0 || idx >= table.value.rows.length) return;
    table.value.rows.splice(idx, 1);
  }

  function updateRow(idx: number, header: string, value: any): void {
    if (idx < 0 || idx >= table.value.rows.length) return;
    if (!table.value.headers.includes(header)) return;

    table.value.rows[idx][header] = value;
  }

  function exportTableToFile(
    table: Table,
    fileType: "xlsx" | "csv" = "xlsx",
    download = true,
  ): File {
    try {
      if (!table || table.rows.length === 0) {
        throw new Error("no data in table can be export");
      }

      const dataForExport = table.rows.map((row) =>
        table.headers.map((header) => row[header] ?? ""),
      );
      const wsData = [table.headers, ...dataForExport];
      const worksheet = XLSX.utils.aoa_to_sheet(wsData);
      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, "Sheet");
      const baseName = table.fileName
        ? table.fileName.replace(/\.[^.]+$/, "")
        : "export";
      const fileName = `${baseName}_edited.${fileType === "xlsx" ? "xlsx" : "csv"}`;
      const content = XLSX.write(workbook, {
        bookType: fileType,
        type: "array",
        ...(fileType === "csv" && { FS: "," }),
      });
      const file = new File([content], fileName, {
        type:
          fileType === "xlsx"
            ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            : "text/csv",
      });

      if (download) {
        XLSX.writeFile(workbook, fileName, {
          bookType: fileType,
          type: "buffer",
          ...(fileType === "csv" && { FS: "," }),
        });
      }

      return file;
    } catch (err) {
      error.value =
        err instanceof Error ? err.message : "export table to file failure";
      throw new Error(`export table to file failure, ${err}`);
    }
  }

  // async function analysisByKMeans(
  //   params: Omit<KMeanReq, "file">,
  // ): Promise<KMeanData | null> {
  //   // 若表没有数据时
  //   if (table.value.rows.length === 0) {
  //     throw new Error("no data in table can be analyzed");
  //   }

  //   // 创建表单数据结构，添加数据项
  //   const formData = new FormData();
  //   formData.append("file", exportTableToFile(table.value, "csv", false));

  //   if (params.k !== undefined) formData.append("k", String(params.k));
  //   if (params.maxIterations !== undefined) {
  //     formData.append("maxIterations", String(params.maxIterations));
  //   }
  //   if (params.tolerance !== undefined) {
  //     formData.append("tolerance", String(params.tolerance));
  //   }
  //   if (params.seed !== undefined) formData.append("seed", String(params.seed));
  //   for (const column of params.columns ?? []) {
  //     formData.append("columns", column);
  //   }

  //   // 接收服务器返回的请求体
  //   const responseBody = await datasetAPI.analysisByKmeans(
  //     formData as unknown as KMeanReq,
  //   );
  //   const responseBodyJson = await responseBody.json();

  //   if (responseBodyJson.code !== 0) {
  //     throw new Error(responseBodyJson.message ?? "K-Means analysis failed");
  //   }

  //   // 返回请求体中的data对象
  //   return responseBodyJson.data ?? null;
  // }

  function isValidFileType(file: File): boolean {
    const validTypes = [
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
      "application/vnd.ms-excel", // .xls
      "text/csv", // .csv
    ];
    // 部分浏览器 MIME 类型不准确，同时检查扩展名
    const validExtensions = [".xlsx", ".xls", ".csv"];

    const extension = file.name
      .substring(file.name.lastIndexOf("."))
      .toLowerCase();

    return (
      validTypes.includes(file.type) || validExtensions.includes(extension)
    );
  }

  function resetError(): void {
    error.value = null;
  }

  function clearTable(): void {
    table.value = { headers: [], rows: [], fileName: "" };
  }

  return {
    table,
    loading,
    error,
    rows,
    cols,
    isEmpty,
    parseFileToTable,
    addRow,
    updateRow,
    deleteRow,
    exportTableToFile,
    // analysisByKMeans,
    isValidFileType,
    resetError,
    clearTable,
  };
});
