import { ElMessage } from "element-plus";

import $api from "@/api/Axios";
import type { ConfigParams, Response } from "@/types/index";

import { ApibaseURL } from "../api";

interface AjaxParams {
    configKey: string; //参数键名
    configName: string; //参数名称
    configType: string; //系统内置
    configValue: string; //参数键值
    remarks: string; //备注
}

export async function apiPostAdd(ajaxParams: AjaxParams) {
    let response: Response = await $api.post(`${ApibaseURL}`, ajaxParams);
    let { code } = response;
    if (code === 200) {
        ElMessage({
            message: "添加成功",
            type: "success",
            center: true,
        });
        return true;
    }
}

export async function apiPutEdit(ajaxParams: ConfigParams) {
    const response: Response = await $api.put(
        `${ApibaseURL}/${ajaxParams.id}`,
        ajaxParams
    );
    const { code } = response;
    if (code === 200) {
        ElMessage({
            message: "修改成功",
            type: "success",
            center: true,
        });
        return true;
    }
}

export async function apiGetDetails(id: string) {
    const response: Response = await $api.get(`${ApibaseURL}/${id}`);
    const { code, data } = response;
    if (code === 200) {
        return data;
    }
}
