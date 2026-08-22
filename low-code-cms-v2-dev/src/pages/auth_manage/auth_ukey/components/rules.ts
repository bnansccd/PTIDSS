import type { FormRules } from "element-plus";
import { charMax, requiredInput, sortFn, sortMax } from "@/tools/rules";
export const rules: FormRules = {
    //  postCode: [requiredInput(), charMax(10)],
    cn: [requiredInput(), charMax(30)],
    sn: [requiredInput(), charMax(30)],
    userId: [requiredInput()],
};
