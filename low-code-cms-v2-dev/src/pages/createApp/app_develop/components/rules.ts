import type { FormRules } from "element-plus";
import { charMax, requiredInput, requiredSelect } from "@/tools/rules";
export const rules: FormRules = {
    roleCode: [requiredInput(), charMax(10)],
    roleName: [requiredInput(), charMax(6)],
    remark: [charMax(20)],
    departIds: [requiredSelect()],
};
