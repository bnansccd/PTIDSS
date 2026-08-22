<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";

import {
    apiPostAdd,
    apiGetDetails,
    apiPutEdit,
    apiGetSort,
    apiPostDomain,
    apiGetDomainDetails,
} from "./api";
import { rules } from "./rules";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    domainName: "", // 域名（最大长度64）正则：（^((?!-)[A-Za-z0-9-]{1,63}(?<!-).)+[A-Za-z]{2,6}$）
    recordInfo: "", // 备案信息（最大长度255）
    recordInfoUrl: "", // 备案信息跳转地址（最大长度128）
    remarks: "",
    universalDomainName: "", // 泛域名（最大长度128）
    // id: props.id,
    id: "",
});

onBeforeMount(async () => {
    if (props.id !== "0") {
        const data = await apiGetDomainDetails(props.id);
        Object.keys(ajaxParams).forEach((item: string) => {
            ajaxParams[item] = data[item];
        });
    } else {
        // 查询排序序号
        // const sort = await apiGetSort();
        // ajaxParams.sort = sort;
    }
});

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
function submit(refElForm: FormInstance | null) {
    if (!refElForm) return;
    refElForm.validate(async (valid, fields) => {
        if (valid) {
            const bool = await apiPostDomain(ajaxParams, ajaxParams.id);

            bool && emit("close");
            bool && emit("query");
        }
    });
}
</script>

<template>
    <GlobalElDialog width="700px" :="$attrs" @close="emit('close')">
        <GlobalElForm
            ref="refElForm"
            label-width="7vw"
            :model="ajaxParams"
            :rules="rules"
        >
            <!-- <GlobalElFormItem label="岗位编码" prop="postCode">
                <el-input
                    v-model="ajaxParams.postCode"
                    placeholder="请输入岗位编码"
                />
            </GlobalElFormItem> -->
            <GlobalElFormItem label="域名" prop="domainName">
                <el-input
                    v-model="ajaxParams.domainName"
                    placeholder="请输入域名"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="泛域名" prop="universalDomainName">
                <el-input
                    v-model="ajaxParams.universalDomainName"
                    placeholder="请输入泛域名"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="备案信息" prop="recordInfo">
                <el-input
                    v-model="ajaxParams.recordInfo"
                    placeholder="请输入备案信息"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="备案信息跳转地址" prop="recordInfoUrl">
                <el-input
                    v-model="ajaxParams.recordInfoUrl"
                    placeholder="请输入备案信息跳转地址"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="备注" prop="remarks">
                <el-input
                    v-model="ajaxParams.remarks"
                    placeholder="请输入备注"
                />
            </GlobalElFormItem>

            <!-- <GlobalElFormItem label="状态" prop="status">
                <GlobalElSwitch
                    v-model="ajaxParams.status"
                    active-value="0"
                    inactive-value="1"
                />
            </GlobalElFormItem> -->

            <!-- <GlobalElFormItem label="排序" prop="sort">
                <el-input
                    type="number"
                    v-model.number="ajaxParams.sort"
                    placeholder="请输入排序值"
                />
            </GlobalElFormItem> -->
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
    title: "配置域名",
    name: "OperatDomain",
});
</script>
