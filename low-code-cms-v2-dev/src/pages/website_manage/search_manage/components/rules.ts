import type { FormRules } from "element-plus";
import { charMax, requiredInput, requiredSelect,sortFn } from "@/tools/rules";
export const rules: FormRules = {
    name: [requiredInput()],
    url: [requiredInput()],
    status: [requiredSelect()],
    keyword: [requiredInput()],
};
