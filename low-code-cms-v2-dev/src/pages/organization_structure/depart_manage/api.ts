import { ElMessage } from "element-plus";
import qs from "qs";
import $api from "@/api/Axios";
import type { Response, orderByDTOSParams } from "@/types/index";
import { filterRecords } from "@/tools/function";

export const ApibaseURL = "/system/api/web/v1/sysDepart";

interface AjaxParams {
    departName?: string; //组织名称
    parentId?: string; //上级组织
    orderByDTOS: Array<orderByDTOSParams>;
}

export const initParams: AjaxParams /*初始化入参*/ = {
    departName: "",
    orderByDTOS: [{ asc: true, column: "sort" }],
};

export async function apiGetList(ajaxParams: AjaxParams) {
    const response: Response = await $api.get(`${ApibaseURL}`, {
        params: ajaxParams,
        paramsSerializer: (ajaxParams) =>
            encodeURI(
                qs.stringify(ajaxParams, {
                    allowDots: true,
                    encode: false,
                })
            ),
    });
    let records: never[] = [];
    const { code, data } = response;
    if (code === 200) {
        records = filterRecords(data);
    }
    return {
        records,
    };
}

export async function apiGetListTree() {
    const response: Response = await $api.get(`${ApibaseURL}/tree`, {});
    let records: never[] = [];
    const { code, data } = response;
    if (code === 200) {
        records = data;
    }
    return records;
}

export  async function apiDelete(ids: string | Array<string>) {
    const response: Response = await $api.delete(
        Array.isArray(ids)
            ? `${ApibaseURL}/${"" + ids}`
            : `${ApibaseURL}/${ids}`
    );
    const { code } = response;
    if (code === 200) {
        ElMessage({
            message: `删除成功${
                Array.isArray(ids) ? ",批量删除只会删除尾项" : ""
            }`,
            type: "success",
            center: true,
        });
        return true;
    }
}


// 同步组织
export const importOrg = async (date: any) => {
    const response: Response = await $api.get(`/system/api/web/v1/sso/syncOrg?tm=${date}`);
    const { code } = response;
    if (code === 200) {
        ElMessage({
            message: `同步成功`,
            type: "success",
            center: true,
        });
        return true;
    }
}