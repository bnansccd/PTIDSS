import type { FormRules } from "element-plus";
import { charMax, requiredInput, requiredSelect, sortMax } from "@/tools/rules";
export const rules: FormRules = {
    departName: [requiredInput(), charMax(30)],
    sort: [requiredInput(), sortMax(100000)],
    // userId: [requiredSelect()],
};
