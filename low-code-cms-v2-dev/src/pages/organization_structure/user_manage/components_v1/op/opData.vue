<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";
import $api from "@/api/Axios";
import { Md5 } from "ts-md5";
import md5 from 'js-md5';

import SsoSelect from "./select/SsoSelect.vue";

import { apiPostAdd, apiGetDetails, apiPutEdit } from "./api";
import { rules } from "./rules";

const props = defineProps<{
    id: string;
    opObject: any;
    rowSelect: any;
    phones: any
}>();

const ajaxParams: any = reactive({
    userId: null,
    sysTarget: "GZT",
    username: null,
    content: null,

    usernameVos: props.phones,
});
console.log(props)
onBeforeMount(async () => {
    if (props.opObject.code !== "add") {
        // const data = await apiGetDetails(props.id);
        Object.keys(ajaxParams).forEach((item: string) => {
            ajaxParams[item] = props.rowSelect[item];
        });
    }
});

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
function submit(refElForm: FormInstance | null) {
    if (!refElForm) return;
    refElForm.validate(async (valid, fields) => {
        if (valid) {
            const params = JSON.parse(JSON.stringify(ajaxParams));
            delete params.usernameVos;
            params.userId = props.id;

            if (ajaxParams.sysTarget == "SSO") {
                params.username =
                    ajaxParams.usernameVos.app_account__account_no;
            }
            if (params.sysTarget == 'GZT') {
                const res:any = await $api.get(
                    `/roadSystem/api/web/v1/gzt/mobileMd5?phoneNum=${md5(props.phones)}`
                );
                const { code, data } = res;
                if (code === 200) {
                    params.username = JSON.parse(data)?.openUserId
                } 
            }
            if (params.username) {
                const bool =
                props.opObject.code === "add"
                    ? await apiPostAdd(params)
                    : await apiPutEdit(params, props.rowSelect.id);

                // const bool = await apiPostAdd(params);
                bool && emit("close");
                bool && emit("query");
            }
        }
    });
}

async function changeVos() {
    const orgs = ajaxParams.usernameVos.orgs;

    if (Array.isArray(orgs) && orgs.length > 0) {
        const org = orgs[0];
        ajaxParams.content = org.idt_org__name;
    }

    if (ajaxParams.sysTarget == "SSO") {
        return null;
    }
    const { code, data } = await $api.get(
        `/roadSystem/api/web/v1/gzt/mobileMd5?phoneNum=${Md5.hashStr(
            ajaxParams.usernameVos.app_account__account_no
        )}`
    );
    if (code == 200) {
        const obj = JSON.parse(data);
        ajaxParams.username = obj.openUserId;
    }
}
</script>

<template>
    <GlobalElDialog :="$attrs" @close="emit('close')">
        <GlobalElForm
            ref="refElForm"
            label-width="8vw"
            :model="ajaxParams"
            :rules="rules"
        >
            <GlobalElFormItem label="系统标识" prop="sysTarget">
                <!-- 系统标识 SSO GZT -->
                <el-radio-group
                    v-model="ajaxParams.sysTarget"
                    @change="
                        () => {
                            ajaxParams.usernameVos = null;
                            ajaxParams.content = null;
                            ajaxParams.username = null;
                            if (ajaxParams.sysTarget == 'GZT') {
                                ajaxParams.usernameVos = $props.phones.phone
                            }
                        }
                    "
                >
                    <el-radio label="SSO">SSO</el-radio>
                    <el-radio label="GZT">GZT</el-radio>
                </el-radio-group>
            </GlobalElFormItem>

            <GlobalElFormItem label="第三方标识" prop="usernameVos">
                <SsoSelect
                    v-if="ajaxParams.sysTarget == 'SSO'" 
                    @change="changeVos"
                    v-model="ajaxParams.usernameVos"
                    placeholder="请输入第三方标识"
                />
                <el-input
                    v-else 
                    v-model="ajaxParams.usernameVos"
                    placeholder="请输入手机号"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="账号备注" prop="content">
                <el-input
                    v-model="ajaxParams.content"
                    placeholder="请输入账号备注"
                />
            </GlobalElFormItem>
        </GlobalElForm>

        <template #footer>
            <el-button type="primary" @click="submit(refElForm)"
                >保存</el-button
            >

            <el-button @click="$emit('close')">取消</el-button>
        </template>
    </GlobalElDialog>
</template>

<style scoped></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "操作数据",
    name: "opData",
});
</script>
