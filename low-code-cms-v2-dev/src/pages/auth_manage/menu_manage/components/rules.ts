import type { FormRules } from "element-plus";
import { charMax, requiredInput, requiredSelect, sortMax } from "@/tools/rules";
export const rules: FormRules = {
    menuName: [requiredInput(), charMax(30)],
    menuCode: [requiredInput(), charMax(60)],
    href: [requiredInput(), charMax(100)],
    menuType: [requiredSelect()],
    sort: [sortMax(100000)],
};
