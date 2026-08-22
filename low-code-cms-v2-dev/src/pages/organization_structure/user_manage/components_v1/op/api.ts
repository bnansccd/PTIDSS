import { ElMessage } from "element-plus";

import $api from "@/api/Axios";
import type { Response } from "@/types/index";

import { ApibaseURL } from "../../api";

interface AjaxParams {
    departId?: number | string | null; //组织id
    email: string; // 邮箱
    headUrl: string; // 头像地址
    phone: string; //手机号
    postIds: Array<number | string>; //岗位
    realName: string; //真实姓名
    sex: number | string | undefined | null; //性别(0女1男)
    username: string; //用户名
}

export async function apiPostAdd(ajaxParams: AjaxParams) {
    const response: Response = await $api.post(
        `/roadSystem/api/web/v1/sso/user`,
        ajaxParams
    );
    const { code } = response;
    if (code === 200) {
        ElMessage({
            message: "添加成功",
            type: "success",
            center: true,
        });
        return true;
    }
}

export async function apiPutEdit(ajaxParams: AjaxParams, id: string) {
    const response: Response = await $api.put(
        `/roadSystem/api/web/v1/sso/user/${id}`,
        ajaxParams
    );
    const { code } = response;
    if (code === 200) {
        ElMessage({
            message: "修改成功",
            type: "success",
            center: true,
        });
        return true;
    }
}

export async function apiGetDetails(id: string) {
    const response: Response = await $api.get(`${ApibaseURL}/detail/${id}`);
    const { code, data } = response;
    if (code === 200) {
        return data;
    }
}

export async function apiPostMenu(id: string, roleIds: Array<string>) {
    // 配置用户角色
    const response: Response = await $api.post(
        `${ApibaseURL}Role/${id}`,
        roleIds
    );
    const { code } = response;
    if (code === 200) {
        ElMessage({
            message: "配置成功",
            type: "success",
            center: true,
        });
        return true;
    }
}
