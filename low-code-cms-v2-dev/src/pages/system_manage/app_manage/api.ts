import { ElMessage } from "element-plus";

import $api from "@/api/Axios";
import type { Response, BaseParams } from "@/types/index";
import { filterRecords } from "@/tools/function";

export const ApibaseURL = "/system/api/web/v1/sysApp";

interface AjaxParams extends BaseParams {
    name: string; //应用名字
}

export const initParams: AjaxParams /*初始化入参*/ = {
    current: 1,
    // size: Number(window.sessionStorage.getItem("Globalpagesize")) || 5,
    size: 100,
    name: "",
};

export async function apiGetList(ajaxParams: AjaxParams) {
    const response: Response = await $api.get(`${ApibaseURL}`, {
        params: ajaxParams,
    });
    let total = 0;
    let records: never[] = [];
    const { code, data } = response;
    if (code === 200) {
        total = data.total * 1;
        records = filterRecords(data.records);
    }
    return {
        total,
        records,
    };
}

export async function apiReset(id: string) {
    const response: Response = await $api.get(`${ApibaseURL}/reset/${id}`);

    const { code, data } = response;
    if (code === 200) {
        ElMessage({
            message: "重置成功",
            type: "success",
            center: true,
        });
        return true;
    } else {
        ElMessage({
            message: "重置失败",
            type: "warning",
            center: true,
        });
        return false;
    }
}
export async function apiLook(id: string) {
    const response: Response = await $api.get(`${ApibaseURL}/getKey/${id}`);

    const { code, data } = response;
    if (code === 200) {
        return data;
    }
}

export async function apiDelete(ids: string | Array<string>) {
    const response: Response = await $api.delete(
        Array.isArray(ids)
            ? `${ApibaseURL}/batch/${"" + ids}`
            : `${ApibaseURL}/${ids}`
    );
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
