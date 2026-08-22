import type { FormRules } from "element-plus";
import {
    charMax,
    requiredInput,
    sortFn,
    sortMax,
    phoneFn,
    domainFn,
    universalDomain,
    websiteUrlFn,
} from "@/tools/rules";
export const rules: FormRules = {
    //  postCode: [requiredInput(), charMax(10)],
    name: [requiredInput(), charMax(30)],
    code: [requiredInput(), charMax(30)],
    startTime: [requiredInput()],
    sort: [requiredInput(), sortMax(100000)],
    remarks: [charMax(255)],
    phone: [requiredInput(), phoneFn()],
    username: [requiredInput(), charMax(30)],
    realName: [requiredInput(), charMax(30)],
    domainName: [requiredInput(), domainFn(), charMax(64)],
    universalDomainName: [requiredInput(), domainFn(), charMax(128)],
    recordInfo: [charMax(255)],
    recordInfoUrl: [charMax(128), websiteUrlFn()],
};

export const appRules: FormRules = {
    appId: [requiredInput()],
    status: [requiredInput()],
    validEndTime: [requiredInput()],
    validStartTime: [requiredInput()],
};
