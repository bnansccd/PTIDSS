import type { FormRules } from "element-plus";
import {
    requiredInput,
    charMax,
    sortFn,
    requiredSelect,
    sortMax,
} from "@/tools/rules";
export const rules: FormRules = {
    name: [requiredInput(), charMax(30)],
    code: [requiredInput(), charMax(30)],
    url: [requiredInput(), charMax(200)],
    status: [requiredSelect()],
    icon: [requiredSelect(), charMax(200)],
    background: [requiredSelect(), charMax(200)],
    type: [requiredSelect()],
    sort: [requiredInput(), sortMax(100000)],
};
