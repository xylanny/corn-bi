import * as fs from "fs";
import * as path from "path";

const OPENAPI_URL = "http://localhost:8080/api/v3/api-docs";
const OUTPUT_DIR = path.join(process.cwd(), "src", "api");
const TYPES_FILE = path.join(OUTPUT_DIR, "types", "index.ts");
const MODULES_DIR = path.join(OUTPUT_DIR, "modules");

interface Schema {
  type?: string;
  format?: string;
  nullable?: boolean;
  properties?: Record<string, Schema>;
  items?: Schema;
  $ref?: string;
  allOf?: Schema[];
}

interface Operation {
  tags?: string[];
  operationId?: string;
  parameters?: Array<{
    name: string;
    in: string;
    required?: boolean;
    schema?: Schema;
  }>;
  requestBody?: { content?: Record<string, { schema?: Schema }> };
  responses?: Record<string, { content?: Record<string, { schema?: Schema }> }>;
}

interface Doc {
  paths: Record<string, Record<string, Operation>>;
  components?: { schemas?: Record<string, Schema> };
}

const toPascal = (s: string) => s.charAt(0).toUpperCase() + s.slice(1);
const cleanRef = (ref: string) => ref.split("/").pop()!;

async function fetchDoc(url: string): Promise<Doc> {
  const res = await fetch(url);
  if (!res.ok) throw new Error(`Fetch failed: ${res.status}`);
  return res.json();
}

function toTS(schema?: Schema): string {
  if (!schema) return "any";
  if (schema.$ref) {
    const name = cleanRef(schema.$ref);
    return name === "ResultVoid" ? "void" : toPascal(name);
  }
  if (schema.allOf) return schema.allOf.map(toTS).join(" & ");
  if (schema.type === "array") return `${toTS(schema.items)}[]`;
  if (schema.type === "object") {
    if (!schema.properties) return "Record<string, any>";
    const props = Object.entries(schema.properties)
      .map(([k, v]) => `${k}${v.nullable ? "?" : ""}: ${toTS(v)}`)
      .join("; ");
    return `{ ${props} }`;
  }
  if (schema.type === "string") return "string";
  if (schema.type === "integer" || schema.type === "number") return "number";
  if (schema.type === "boolean") return "boolean";
  return "any";
}

function generateTypes(schemas: Record<string, Schema>): string {
  const lines: string[] = [
    "",
    "export interface Result<T = any> {",
    "  code: number;",
    "  message: string;",
    "  data: T;",
    "}",
    "",
  ];
  for (const [name, schema] of Object.entries(schemas)) {
    if (name === "ResultVoid") continue;
    const ts = toTS(schema);
    if (schema.type === "object" && schema.properties) {
      lines.push(`export interface ${toPascal(name)} {`);
      for (const [k, v] of Object.entries(schema.properties)) {
        lines.push(`  ${k}${v.nullable ? "?" : ""}: ${toTS(v)};`);
      }
      lines.push("}\n");
    } else {
      lines.push(`export type ${toPascal(name)} = ${ts};\n`);
    }
  }
  return lines.join("\n");
}

function generateModules(doc: Doc): void {
  const groups = new Map<
    string,
    Array<{ path: string; method: string; op: Operation }>
  >();
  for (const [pathUrl, methods] of Object.entries(doc.paths)) {
    for (const [method, op] of Object.entries(methods)) {
      for (const tag of op.tags || ["default"]) {
        if (!groups.has(tag)) groups.set(tag, []);
        groups.get(tag)!.push({ path: pathUrl, method, op });
      }
    }
  }

  if (!fs.existsSync(MODULES_DIR))
    fs.mkdirSync(MODULES_DIR, { recursive: true });

  for (const [tag, items] of groups) {
    // 生成模块名（用于文件名和对象名）
    const moduleName = tag.replace(/-controller$/, "").replace(/-/g, "_");

    // !!!
    const apiName = moduleName + "API";

    const lines: string[] = ["import client from '@/api/client';"];
    const types = new Set<string>();
    const funcs: string[] = [];

    for (const { path: pathUrl, method, op } of items) {
      const fn =
        op.operationId ||
        `${method}${pathUrl.replace(/[{}]/g, "").replace(/\//g, "_")}`;
      const resp = op.responses?.["200"]?.content?.["*/*"]?.schema?.$ref;
      const respType = resp ? cleanRef(resp) : "void";
      if (respType !== "ResultVoid") types.add(toPascal(respType));

      const body = op.requestBody?.content?.["application/json"]?.schema;
      const bodyRef = body?.$ref;
      const query = (op.parameters || []).filter((p) => p.in === "query");
      const pathP = (op.parameters || []).filter((p) => p.in === "path");

      const args: string[] = [];
      let url = pathUrl;
      let hasBody = false;

      if (bodyRef) {
        const t = toPascal(cleanRef(bodyRef));
        args.push(`data: ${t}`);
        types.add(t);
        hasBody = true;
      } else if (body?.type === "object" && body.properties) {
        args.push("data: Record<string, any>");
        hasBody = true;
      }

      if (pathP.length) {
        args.push(
          `path: { ${pathP.map((p) => `${p.name}: ${p.schema?.type || "string"}`).join("; ")} }`,
        );
        for (const p of pathP)
          url = url.replace(`{${p.name}}`, `\${path.${p.name}}`);
      }

      if (query.length) {
        const props = query
          .map((p) => `${p.name}?: ${p.schema?.type || "string"}`)
          .join("; ");
        args.push(`query: { ${props} } = {}`);
        url +=
          "?" + query.map((p) => `${p.name}=\${query.${p.name}}`).join("&");
      }

      const returnType =
        respType === "ResultVoid"
          ? "Promise<void>"
          : `Promise<${toPascal(respType)}>`;
      const bodyArg = hasBody ? ", data" : "";
      const methodCall = method === "get" ? "get" : method;
      const urlArg = `\`${url}\``;

      funcs.push(`  ${fn}: (${args.join(", ")}): ${returnType} => {`);
      funcs.push(`    return client.${methodCall}(${urlArg}${bodyArg});`);
      funcs.push(`  },`);
    }

    const imports = Array.from(types)
      .filter((t) => t !== "ResultVoid")
      .join(", ");
    if (imports) lines.push(`import type { ${imports} } from '../types';`);

    lines.push("", `export const ${apiName} = {`, ...funcs, "};");

    fs.writeFileSync(
      path.join(MODULES_DIR, `${moduleName}.ts`),
      lines.join("\n"),
    );
  }
}

async function main() {
  try {
    console.log("📡 获取 OpenAPI...");
    const doc = await fetchDoc(OPENAPI_URL);
    const schemas = doc.components?.schemas || {};

    if (!fs.existsSync(path.dirname(TYPES_FILE)))
      fs.mkdirSync(path.dirname(TYPES_FILE), { recursive: true });
    fs.writeFileSync(TYPES_FILE, generateTypes(schemas));
    console.log("@类型:", TYPES_FILE);

    generateModules(doc);
    console.log("@模块:", MODULES_DIR);
    console.log("@完成");
  } catch (e) {
    console.error("@", e);
  }
}

main();
