import type { FormRules } from "element-plus";
import { charMax, requiredInput, requiredSelect,sortFn } from "@/tools/rules";
export const rules: FormRules = {
    urls: [requiredSelect()],
    status: [requiredInput()],
    title: [requiredInput(), charMax(20)],
};
