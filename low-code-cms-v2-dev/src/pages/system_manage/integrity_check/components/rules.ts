import type { FormRules } from "element-plus";
import { charMax, requiredInput } from "@/tools/rules";
export const rules: FormRules = {
    configKey: [requiredInput(), charMax(10)],
    configName: [requiredInput(), charMax(10)],
    configType: [requiredInput(), charMax(10)],
    configValue: [requiredInput(), charMax(10)],
    remarks: [requiredInput(), charMax(10)],
};
