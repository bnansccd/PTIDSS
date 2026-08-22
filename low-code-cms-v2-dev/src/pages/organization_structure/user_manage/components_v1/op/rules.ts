import type { FormRules } from "element-plus";
import { charMax, requiredInput, requiredSelect, phoneFn } from "@/tools/rules";
export const rules: FormRules = {
    usernameVos: [requiredSelect()],
    sysTarget: [requiredSelect(), charMax(256)],
    content: [requiredInput(), charMax(256)],
    // departId: [requiredSelect()],
    // postIds: [requiredSelect()],
    // roles: [requiredSelect()],
};
