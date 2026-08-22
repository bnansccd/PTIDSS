import type { FormRules } from "element-plus";
import { charMax, requiredInput, sortFn, sortMax } from "@/tools/rules";
export const rules: FormRules = {
    //  postCode: [requiredInput(), charMax(10)],
    postName: [requiredInput(), charMax(30)],
    sort: [requiredInput(), sortMax(100000)],
    remarks: [charMax(200)],
};
