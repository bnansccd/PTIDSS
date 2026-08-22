import type { FormRules } from "element-plus";
import { charMax, requiredInput, requiredSelect,sortFn } from "@/tools/rules";
export const rules: FormRules = {
    photoUrl: [requiredInput()],
    status: [requiredInput()],
    summary: [requiredInput(), charMax(50)],
    text: [requiredInput()],
    title: [requiredInput(), charMax(20)]

};
