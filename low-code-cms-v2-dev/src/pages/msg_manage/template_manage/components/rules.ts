import type { FormRules } from "element-plus";
import { charMax, requiredInput, sortFn } from "@/tools/rules";
export const rules: FormRules = {
  //  postCode: [requiredInput(), charMax(10)],
    postName: [requiredInput(), charMax(20)],
    sort: [requiredInput()],
    remarks: [requiredInput(), charMax(20)],
};
