export function isNonNumeric(value: unknown): boolean {
  if (value === null || value === undefined || value === "") {
    return false;
  }

  return !Number.isFinite(
    typeof value === "number" ? value : Number(value),
  );
}