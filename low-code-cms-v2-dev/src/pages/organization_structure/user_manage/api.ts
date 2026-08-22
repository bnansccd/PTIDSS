import { ElMessage } from "element-plus";
import qs from "qs";
import $api from "@/api/Axios";
import type { Response, BaseParams, orderByDTOSParams } from "@/types/index";
import { filterRecords } from "@/tools/function";

export const ApibaseURL = "/system/api/web/v1/sysUser";

interface AjaxParams extends BaseParams {
    username: string | null; //用户名
    realName: string | null;
    phone: string | null;
    departId: string | null;
    orderByDTOS: Array<orderByDTOSParams>;
}

export const initParams: AjaxParams /*初始化入参*/ = {
    current: 1,
    size: Number(window.sessionStorage.getItem("Globalpagesize")) || 10,
    username: "",
    realName: "",
    phone: "",
    departId: "",
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

export async function apiDelete(ids: string | Array<string>) {
    const response: Response = await $api.delete(
        Array.isArray(ids)
            ? `${ApibaseURL}/${"" + ids}`
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

export async function apiStatus(ids: string | Array<string>, status?: number) {
    const response: Response = await $api.patch(
        Array.isArray(ids)
            ? `${ApibaseURL}/batch/${"" + ids}`
            : `${ApibaseURL}/${ids}`,
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

export async function apiReastPassword(userId: string) {
    // 密码重置
    const response: Response = await $api.put(
        `${ApibaseURL}/password/reset/${userId}`
    );
    const { code, data } = response;
    if (code === 200) {
        ElMessage({
            message: `密码重置成功,重置密码为${data}`,
            type: "success",
            center: true,
        });
        return true;
    }
}

interface Password {
    newPassword: string;
    oldPassword: string;
    verifyPassword: string;
}

export async function apiEditPassword(ajaxParams: Password) {
    const fromData = new FormData();
    fromData.append("oldPassword", ajaxParams.oldPassword);
    fromData.append("newPassword", ajaxParams.newPassword);

    // 修改密码
    const response: Response = await $api.post(
        `${ApibaseURL}/password/change/`,
        fromData,
        {
            headers: { "Content-Type": "multipart/form-data" },
        }
    );
    const { code } = response;
    if (code === 200) {
        ElMessage({
            message: `密码修改成功,3秒后将退出登录,请用新密码重新登录`,
            type: "success",
            center: true,
            duration: 2000,
        });
        return true;
    }
    return false;
}

// 同步人员
export const importUser = async (date: any) => {
    const response: Response = await $api.get(
        `/system/api/web/v1/sso/syncUsers?tm=${date}`
    );
    const { code } = response;
    if (code === 200) {
        ElMessage({
            message: `同步成功`,
            type: "success",
            center: true,
        });
        return true;
    }
};
