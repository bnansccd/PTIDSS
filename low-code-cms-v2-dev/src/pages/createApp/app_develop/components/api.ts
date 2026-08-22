import { ElMessage } from "element-plus";

import $api from "@/api/Axios";
import type { Response } from "@/types/index";

import { ApibaseURL } from "../api";

interface AjaxParams {
    roleCode: string; //角色编码
    roleName: string; //角色名称
    sort: number | null; //排序
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
export async function apiGetSort() {
    const response: Response = await $api.get(`${ApibaseURL}/sort`);
    const { code, data } = response;
    if (code === 200) {
        return data;
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

export async function apiPutAuth(ajaxParams: any, id: string) {
    // 配置数据权限
    let response: Response;
    // eslint-disable-next-line prefer-const
    response = await $api.put(`${ApibaseURL}/dataRange/${id}`, ajaxParams);
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

export async function apiPostMenu(ajaxParams: any, id: string) {
    // 配置角色菜单
    let response: Response;
    // eslint-disable-next-line prefer-const
    response = await $api.post(
        `/system/api/web/v1/sysRoleMenu/${id}`,
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
