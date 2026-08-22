import { ElMessage } from "element-plus";

import $api from "@/api/Axios";
import type { Response } from "@/types/index";

import { ApibaseURL } from "../api";

interface AjaxParams {
    parentId: number | string | null; // 父级菜单id
    menuName: string; //  菜单名称
    menuCode: string; //  权限标识
    href: string; // 路由地址
    icon?: string; // 图标
    menuType: number | string | null; //   菜单类型（0左侧菜单1顶部菜单2按钮）
    isShow?: number | string | null; //  是否展示(0展示1隐藏)
    status?: number | string | null; //启用停用(0启用1停用)
    sort: number | string | null; //排序
    appId?: string; // 应用id
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
export async function apiGetDetails(id: string) {
    const response: Response = await $api.get(`${ApibaseURL}/${id}`);
    const { code, data } = response;
    if (code === 200) {
        return data;
    }
}
