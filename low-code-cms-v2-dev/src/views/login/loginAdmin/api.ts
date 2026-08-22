import axios from "@/api/Axios";
import $api from "@/api/Axios";
import type { Response } from "@/types/index";
const ApibaseURL = "/auth";

interface AjaxParams {
    username: string;
    password: string;
    code?: string;
    isSave?: boolean;
}

export const login = function (ajaxParams: AjaxParams) {
    axios.post(`${ApibaseURL}/api/web/v1/login`, ajaxParams);
};
export async function apiDomain() {
    const response: Response = await $api.get(
        `/system/api/rpc/v1/domainName/info`
    );
    const { code, data } = response;
    if (code === 200) {
        return data;
    }
}
