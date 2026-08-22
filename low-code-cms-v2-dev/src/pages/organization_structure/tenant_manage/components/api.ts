import { ElMessage } from "element-plus";

import $api from "@/api/Axios";
import type { Response } from "@/types/index";

import { ApibaseURL } from "../api";

interface AjaxParams {
    code: string;
    name: string;
    status: string;
    startTime: string;
    endTime: string;
    phone?: string;
    username?: string;
    realName?: string;
    id?: string;
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
export async function apiGetSort(parentId: string) {
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

/**
 * 获取应用列表
 */
export async function apiGetAppList(id: string) {
    const response: Response = await $api.get(
        `/system/api/web/v1/sysTenant/getApp?tenantId=${id}`
    );
    const { code, data } = response;
    if (code === 200) {
        return data;
    }
}

/**
 *
 * 绑定app
 * @param ajaxParams {
 *     appId: "", // 应用ID
 *     status: "", // 状态（0：停用；1：正常）
 *     tenantId: props.id, // 租户ID
 *     validEndTime: "", // 有效期结束时间（格式：YYYY-MM-DD HH:mm:ss）
 *     validStartTime: "", // 有效期开始时间（格式：YYYY-MM-DD HH:mm:ss）
 * }
 * @returns
 */
export async function apiBindApp(ajaxParams: any) {
    const response: Response = await $api.post(
        `/system/api/web/v1/sysTenant/bindApp`,
        ajaxParams
    );
    const { code } = response;
    if (code === 200) {
        ElMessage({
            message: "绑定成功",
            type: "success",
            center: true,
        });
        return true;
    }
}

/**
 * 更新租户APP绑定
 * /api/web/v1/sysTenant/bindApp/{id}
 */
export async function apiUpdateApp(id: string, ajaxParams: any) {
    const response: Response = await $api.put(
        `/system/api/web/v1/sysTenant/bindApp/${id}`,
        ajaxParams
    );
    const { code } = response;
    if (code === 200) {
        ElMessage({
            message: "操作成功",
            type: "success",
            center: true,
        });
        return true;
    }
}

/**
 * 解绑app
 * @param id
 * @returns
 */
export async function apiUnBindApp(ajaxParams: any) {
    const response: Response = await $api.post(
        `/system/api/web/v1/sysTenant/unbindApp/`,
        ajaxParams
    );
    const { code } = response;
    if (code === 200) {
        ElMessage({
            message: "解绑成功",
            type: "success",
            center: true,
        });
        return true;
    }
}

export async function apiGetMenuIds(id: string) {
    const response: Response = await $api.get(
        `/system/api/web/v1/sysMenu/tenantId/${id}`
    );
    const { code, data } = response;
    if (code === 200) {
        return data;
    }
}

export async function apiGetAppMenuIds(appId: string, tenantId: string) {
    const response: Response = await $api.get(
        `/system/api/web/v1/sysTenant/getAppMenu?appId=${appId}&tenantId=${tenantId}`
    );
    const { code, data } = response;
    if (code === 200) {
        return data;
    }
}

export async function apiGetAppAllMenuIds(id: string) {
    const response: Response = await $api.get(
        `/system/api/web/v1/sysMenu/tree/${id}`
    );
    const { code, data } = response;
    if (code === 200) {
        return data;
    }
}

export async function apiPostMenu(
    ajaxParams: any,
    tenantId: string,
    appId: string
) {
    const checkIds = ajaxParams.checkIds.map((item: string) => ({
        menuId: item,
        appId,
    }));
    // 配置角色菜单
    let response: Response;
    // eslint-disable-next-line prefer-const
    response = await $api.post(
        `/system/api/web/v1/sysTenant/bindMenu/${tenantId}`,
        checkIds
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

export async function apiPostDomain(ajaxParams: any, id: string) {
    // 配置角色菜单
    let response: Response;
    // eslint-disable-next-line prefer-const
    response = await $api.put(`/system/api/web/v1/domain/${id}`, ajaxParams);
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

export async function apiGetDomainDetails(id: string) {
    const response: Response = await $api.get(
        `/system/api/web/v1/domain/tenantId/${id}`
    );
    const { code, data } = response;
    if (code === 200) {
        return data;
    }
}
