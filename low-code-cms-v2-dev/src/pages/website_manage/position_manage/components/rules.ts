import type { FormRules } from "element-plus";
import { charMax, requiredInput, requiredSelect,sortFn } from "@/tools/rules";
export const rules: FormRules = {
    status: [requiredSelect()],
    postName: [requiredInput()],
    departmentId: [requiredSelect()],
    jobDetail: [requiredInput()],
    num: [requiredInput()],
};
