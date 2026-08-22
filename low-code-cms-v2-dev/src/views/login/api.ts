import axios from "@/api/Axios";
import $api from "@/api/Axios";
const ApibaseURL = "/auth";
import { baseUrl, baseStaticUrl } from '@/env/index'
import type { Response } from "@/types/index";
import { ElMessage } from 'element-plus'
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

export async function apiCode() {
    const response = await fetch(baseUrl + `/auth/api/web/v1/captcha`, {
        method: 'get',
        headers: {
            'Content-Type': 'image/jpeg'
        }
    })
    if (!response.ok) {
        throw new Error(`Network response was not ok: ${response.statusText}`);
    }
    
    return response;
}
export async function getPhoneCode(params:any) {
    const response: any = await $api.post(
        `/auth/api/web/v1/captcha/msg?phone=${params.phone}&code=${params.code}&requestId=${params.requestid}`
    );
    const { code, data } = response;
    if (code === 200) {
        return true;
    } else {
        return code == '10020' ? '10020' : false
    }
}
export async function apiRandom() {
    const response: any = await $api.post(
        `/auth/api/data/ukey/v1/random`
    );
    const { code, data } = response;
    if (code === 200) {
        return response;
    }
}

export async function submitUsb (data:any) {
    const res: Response = await $api.post(
        `/auth/api/data/ukey/v1/login`,data
    );
   
    if (res.code === 200) {
        return res;
    }
} 