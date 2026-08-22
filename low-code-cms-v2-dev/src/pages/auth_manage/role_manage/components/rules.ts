import type { FormRules } from "element-plus";
import { charMax, requiredInput, requiredSelect, sortMax } from "@/tools/rules";
export const rules: FormRules = {
    roleCode: [requiredInput(), charMax(30)],
    roleName: [requiredInput(), charMax(30)],
    remark: [charMax(200)],
    departIds: [requiredSelect()],
    sort: [sortMax(100000)],
};
