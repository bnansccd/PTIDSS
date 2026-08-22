import { ElMessage } from "element-plus";
import qs from "qs";
import $api from "@/api/Axios";
import type { orderByDTOSParams, Response } from "@/types/index";
import { filterRecords } from "@/tools/function";

export const ApibaseURL = "/system/api/web/v1/sysMenu";

interface AjaxParams {
    menuName?: string; // 菜单名称
    appId?: string; // 应用id
    orderByDTOS: Array<orderByDTOSParams>;
}

export const initParams: AjaxParams /*初始化入参*/ = {
    menuName: "",
    appId: "",
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

export async function apiDelete(ids: string | Array<string>) {
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

export async function apiStatus(ids: string | Array<string>, status?: number) {
    const response: Response = await $api.patch(
        Array.isArray(ids)
            ? `${ApibaseURL}/batch/${"" + ids}`
            : `${ApibaseURL}/${ids}`,
        null,
        {
            // params: {
            //     status: status,
            // },
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

export async function apiShow(ids: string | Array<string>, status?: number) {
    const response: Response = await $api.patch(
        Array.isArray(ids)
            ? `${ApibaseURL}/isShow/batch/${"" + ids}`
            : `${ApibaseURL}/isShow/${ids}`,
        null,
        {
            // params: {
            //     status: status,
            // },
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

export async function apiDrag(id: string, parentId?: string) {
    const response: Response = await $api.put(
        `${ApibaseURL}/drag/${id}`,
        null,
        {
            params: {
                parentId: parentId,
            },
        }
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
