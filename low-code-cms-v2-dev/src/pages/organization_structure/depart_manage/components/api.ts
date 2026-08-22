import { ElMessage } from "element-plus";

import $api from "@/api/Axios";
import type { Response } from "@/types/index";

import { ApibaseURL } from "../api";

interface AjaxParams {
    departName: string; //组织名字
    parentId: number | string | null; //上级组织id
    sort: number | string | null; //排序
}

export async function apiPostAdd(ajaxParams: AjaxParams) {
    const response: Response = await $api.post(`${ApibaseURL}`, ajaxParams);
    const { code } = response;
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
    const response: Response = await $api.put(
        `${ApibaseURL}/${id}`,
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

export async function apiGetSort(parentId: string) {
    const response: Response = await $api.get(`${ApibaseURL}/sort`, {
        params: {
            parentId: parentId,
        },
    });
    const { code, data } = response;
    if (code === 200) {
        return data;
    }
}

export async function apiPostMenu(id: string, roleIds: Array<string>) {
    // 配置组织角色
    const response: Response = await $api.post(
        `${ApibaseURL}Role/${id}`,
        roleIds
    );
    const { code } = response;
    if (code === 200) {
        ElMessage({
            message: "配置成功",
            type: "success",
            center: true,
        });
        return true;
    }
}
