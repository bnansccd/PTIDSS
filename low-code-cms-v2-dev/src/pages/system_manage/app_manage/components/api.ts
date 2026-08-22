import { ElMessage } from "element-plus";

import $api from "@/api/Axios";
import type { Response } from "@/types/index";

import { ApibaseURL } from "../api";

export interface AppParams {
    background: string; // 背景图
    icon: string; // 图标
    name: string; // 名称
    code?: string;
    status: string; // 启用状态 0未启用 1启用
    url: string; // 地址
    sort?: number | string; //排序
    id?: string;
    type: string;
    isActive?: boolean;
}

export async function apiPostAdd(ajaxParams: AppParams) {
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

export async function apiPutEdit(ajaxParams: AppParams, id: string) {
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
export async function apiBindApp(ajaxParams: any, id: string) {
    // 配置角色菜单
    let response: Response;
    // eslint-disable-next-line prefer-const
    response = await $api.put(
        `/system/api/web/v1/sysMenu/bindApp/${id}`,
        ajaxParams.checkIds
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
