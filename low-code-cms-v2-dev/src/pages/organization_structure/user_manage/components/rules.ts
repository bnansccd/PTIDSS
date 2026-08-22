import type { FormRules } from "element-plus";
import { charMax, requiredInput, requiredSelect, phoneFn } from "@/tools/rules";
export const rules: FormRules = {
    username: [requiredInput(), charMax(30)],
    realName: [requiredInput(), charMax(30)],
    phone: [requiredInput(), charMax(11), phoneFn()],
    // departId: [requiredSelect()],
    // postIds: [requiredSelect()],
    // roles: [requiredSelect()],
};
