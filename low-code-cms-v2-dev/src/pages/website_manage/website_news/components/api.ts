import { ElMessage } from "element-plus";

import $api from "@/api/Axios";
import type { Response } from "@/types/index";

import { ApibaseURL } from "../api";

interface AjaxParams {
    postCode: string; //岗位编码
    postName: string; //岗位名称
    remarks: string; //备注
    sort: number | string; //排序
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

export async function apiPutEdit(ajaxParams: AjaxParams, id: string) {
    let response: Response = await $api.put(`${ApibaseURL}/${id}`, ajaxParams);
    let { code } = response;
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
    let response: Response = await $api.get(`${ApibaseURL}/${id}`, { id: id });
    let { code, data } = response;
    if (code === 200) {
        return data;
    }
}

export async function apiDelete(ids: string | Array<string>) {
    const response: Response = await $api.delete(`${ApibaseURL}/${"" + ids}`);
    const { code } = response;
    if (code === 200) {
        ElMessage({
            message: "删除成功",
            type: "success",
            center: true,
        });
        return true;
    }
}
