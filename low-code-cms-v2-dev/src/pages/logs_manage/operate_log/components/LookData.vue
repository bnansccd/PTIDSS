<script setup lang="ts">
import { reactive, onBeforeMount } from "vue";

import { apiGetDetails } from "./api";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    businessType: null, //业务类型（0其它1新增2修改3删除）
    businessTypeName: "", //业务类型（0其它1新增2修改3删除）
    deptName: "", //组织名称
    errorMsg: "", //错误消息
    jsonResult: "", //返回参数
    method: "", //方法名称
    operIp: "", //主机地址
    operLocation: "", //操作地点
    operName: "", //操作人员
    operParam: "", //请求参数
    operTime: "", //操作时间
    operUrl: "", //请求URL
    operatorType: null, //操作类别（0其它，1后台用户，2手机端用户）
    operatorTypeName: "", //操作类别（0其它，1后台用户，2手机端用户）
    requestMethod: "", //请求方式
    status: null, //操作状态
    title: "", //模块标题
});

onBeforeMount(async () => {
    let data = await apiGetDetails(props.id);
    Object.keys(ajaxParams).forEach((item: string) => {
        ajaxParams[item] = data[item];
    });
});
</script>

<template>
    <GlobalElDialog title="查看" :="$attrs" @close="$emit('close')">
        <GlobalElForm ref="refElForm" label-width="5vw">
            <GlobalElFormItem label="业务类型" prop="">
                {{ ajaxParams.businessTypeName }}
            </GlobalElFormItem>

            <GlobalElFormItem label="组织名称" prop="">
                {{ ajaxParams.deptName }}
            </GlobalElFormItem>

            <GlobalElFormItem label="错误消息" prop="">
                {{ ajaxParams.errorMsg }}
            </GlobalElFormItem>

            <GlobalElFormItem label="返回参数" prop="">
                {{ ajaxParams.jsonResult }}
            </GlobalElFormItem>

            <GlobalElFormItem label="方法名称" prop="">
                {{ ajaxParams.method }}
            </GlobalElFormItem>

            <GlobalElFormItem label="主机地址" prop="">
                {{ ajaxParams.operIp }}
            </GlobalElFormItem>

            <GlobalElFormItem label="操作地点" prop="">
                {{ ajaxParams.operLocation }}
            </GlobalElFormItem>

            <GlobalElFormItem label="操作人员" prop="">
                {{ ajaxParams.operName }}
            </GlobalElFormItem>

            <GlobalElFormItem label="请求参数" prop="">
                {{ ajaxParams.operParam }}
            </GlobalElFormItem>

            <GlobalElFormItem label="操作时间" prop="">
                {{ ajaxParams.operTime }}
            </GlobalElFormItem>

            <GlobalElFormItem label="请求URL" prop="">
                {{ ajaxParams.operUrl }}
            </GlobalElFormItem>

            <GlobalElFormItem label="操作类别" prop="">
                {{ ajaxParams.operatorTypeName }}
            </GlobalElFormItem>

            <GlobalElFormItem label="请求方式" prop="">
                {{ ajaxParams.requestMethod }}
            </GlobalElFormItem>

            <GlobalElFormItem label="操作状态" prop="">
                {{ ajaxParams.status }}
            </GlobalElFormItem>

            <GlobalElFormItem label="模块标题" prop="">
                {{ ajaxParams.title }}
            </GlobalElFormItem>
        </GlobalElForm>
    </GlobalElDialog>
</template>

<style scoped></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "查看详情",
    name: "LookData",
});
</script>
