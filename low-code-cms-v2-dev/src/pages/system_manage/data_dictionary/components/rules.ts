import type { FormRules } from "element-plus";
import { requiredInput, charMax, sortFn, sortMax } from "@/tools/rules";
export const rules: FormRules = {
    dictName: [requiredInput(), charMax(60)],
    dictType: [requiredInput(), charMax(60)],
    remarks: [charMax(256)],
    sort: [sortMax(100000)],
};
