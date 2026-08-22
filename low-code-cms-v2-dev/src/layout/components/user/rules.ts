import type { FormRules } from "element-plus";
import { requiredInput } from "@/tools/rules";
export const rules: FormRules = {
    oldPassword: {
        required: true,
        message: "请输入原密码",
        trigger: "change",
    },
    newPassword: [requiredInput(), passwordFn()],
    verifyPassword: [requiredInput(), passwordFn()],
};

function passwordFn() {
    return {
        trigger: "change",

        validator: (rule: any, value: any, callback: any) => {
            console.log(rule,'')
            if (
                !/^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*.]).{8,16}$/.test(
                    value
                )
            ) {
                callback(new Error("请输入8到16位且包含大小写字母的密码"));
            } else {
                callback();
            }
        },
    };
}
