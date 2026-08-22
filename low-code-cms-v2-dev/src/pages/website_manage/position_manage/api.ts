import { ElMessage } from "element-plus";

import $api from "@/api/Axios";
import type { Response, BaseParams } from "@/types/index";
import { filterRecords } from "@/tools/function";

export const ApibaseURL = "/erp/api/web/post";

interface AjaxParams extends BaseParams {
    postName: string | null; //岗位名
}

export const initParams: AjaxParams /*初始化入参*/ = {
    current: 1,
    size: Number(window.sessionStorage.getItem("Globalpagesize")) || 10,
    postName: "",
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

export async function apiStatus(ids: string | Array<string>, status?: number) {
    const response: Response = await $api.patch(
        Array.isArray(ids)
            ? `${ApibaseURL}/status/batch/${"" + ids}`
            : `${ApibaseURL}/status/${ids}`,
        null,
        {
            params: {
                status: status,
            },
        }
    );
    const { code } = response;
    if (code === 200) {
        ElMessage({
            message: `操作成功`,
            type: "success",
            center: true,
        });
        return true;
    }
}
