<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";

import $api from "@/api/Axios";
import { apiPostAdd, apiGetDetails, apiPutEdit, apiGetSort } from "./api";
import { rules } from "./rules";

const props = defineProps<{
    id: string;
}>();

const ajaxParams:any = reactive({
    // postCode: "",
    cn: "",
    sn: "",
    userId: ""
});

onBeforeMount(async () => {
    if (props.id !== "0") {
        let data = await apiGetDetails(props.id);
        Object.keys(ajaxParams).forEach((item: string) => {
            ajaxParams[item] = data[item];
        });
        
        getUser(data.userVO?.username)
    }
});

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
function submit(refElForm: FormInstance | null) {
    if (!refElForm) return;
    refElForm.validate(async (valid, fields) => {
        if (valid) {
            let bool =
                props.id === "0"
                    ? await apiPostAdd(ajaxParams)
                    : await apiPutEdit(ajaxParams, props.id);
            bool && emit("close");
            bool && emit("query");
        }
    });
}

const userloading = ref(false)
const options:any = ref([])
const getUser = async (query?:string) => {510184199605305573
    userloading.value = true
    const res: any = await $api.get(`/system/api/web/v1/sysUser`, {
        params: {
            username: query,
            current: 1,
            size: 50
        },
    });
    if (res.code == 200) {
        options.value = res.data.records
        console.log(res.data)
    } else {
        options.value = []
    }
    userloading.value = false
}
const remoteMethod = async (query: string) => {
  if (query) {
    getUser(query)
  }
}

// 测试陈兴林
// 510184199605305573
</script>

<template>
    <GlobalElDialog :="$attrs" @close="emit('close')">
        <GlobalElForm
            ref="refElForm"
            label-width="7vw"
            :model="ajaxParams"
            :rules="rules"
        >

            <GlobalElFormItem label="证书持有人(CN)" prop="cn">
                <el-input
                    v-model="ajaxParams.cn"
                    placeholder="请输入CN"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="证书号码(SN)" prop="sn">
                <el-input
                    v-model="ajaxParams.sn"
                    placeholder="请输入SN"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="证书使用用户" prop="userId">
                <el-select
                    v-model="ajaxParams.userId"
                    filterable
                    remote
                    reserve-keyword
                    placeholder="请选择证书使用用户"
                    :remote-method="remoteMethod"
                    :loading="userloading"
                    style="width: 240px"
                >
                    <el-option
                        v-for="item in options"
                        :key="item.sysUserVO?.id"
                        :label="item.sysUserVO?.username"
                        :value="item.sysUserVO?.id"
                    />
                </el-select>
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
    title: "数据操作",
    name: "OperatData",
});
</script>
