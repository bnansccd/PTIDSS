import type { FormRules } from "element-plus";
import { charMax,verifyPhone } from '@/tools/rules'
export const rules: FormRules = {
    username: {
        required: true,
        message: "请输入账号",
        trigger: "change",
    },
    password: {
        required: true,
        message: "请输入密码",
        trigger: "change",
    },
    code: {
        required: true,
        message: "请输入验证码",
        trigger: "change",
    },
};


export const ruleCode: FormRules = {
    phone: [{
        required: true,
        message: "请输入手机号",
        trigger: "change",
    }, charMax(11), verifyPhone(true)],
    code: [{
        required: true,
        message: "请输入手机验证码",
        trigger: "change",
    }, charMax(6)],
    phoneCode: [{
        required: true,
        message: "请输入图中的验证码",
        trigger: "change",
    }, charMax(4)]
}