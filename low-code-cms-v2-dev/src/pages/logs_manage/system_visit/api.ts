import $api from "@/api/Axios";
import type { Response, BaseParams, orderByDTOSParams } from "@/types/index";
import { filterRecords } from "@/tools/function";
import qs from "qs";
export const ApibaseURL = "/system/api/web/v1/sysLogininfor";

interface AjaxParams extends BaseParams {
    userName: string; //用户账户
    startTime: string;
    endTime: string;
    orderByDTOS: Array<orderByDTOSParams>;
}

export const initParams: AjaxParams /*初始化入参*/ = {
    current: 1,
    size: Number(window.sessionStorage.getItem("Globalpagesize")) || 10,
    userName: "",
    startTime: "",
    endTime: "",
    orderByDTOS: [],
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
