import { ElMessage } from "element-plus";
import qs from "qs";
import $api from "@/api/Axios";
import type { Response, BaseParams, orderByDTOSParams } from "@/types/index";
import { filterRecords } from "@/tools/function";

export const ApibaseURL = "/system/api/web/v1/sysPost";

interface AjaxParams extends BaseParams {
    postName: string | null; //岗位名
    orderByDTOS?: Array<orderByDTOSParams>;
}

export const initParams: AjaxParams /*初始化入参*/ = {
    current: 1,
    size: Number(window.sessionStorage.getItem("Globalpagesize")) || 10,
    postName: "",
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
