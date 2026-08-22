import axios from "@/api/Axios";

const baseURL = "/system";

interface AjaxParams {
    username: string;
    password: string;
    code?: string;
    checked?: boolean;
}

export const register = function (ajaxParams: AjaxParams) {
    axios.post(`${baseURL}/api/rpc/v1/sysUser/register`, ajaxParams);
};
