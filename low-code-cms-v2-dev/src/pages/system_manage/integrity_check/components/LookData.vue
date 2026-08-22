<script setup lang="ts">
import { reactive, onBeforeMount } from "vue";

import { apiGetDetails } from "./api";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    configKey: "",
    configName: "",
    configType: "",
    configValue: "1",
    remarks: "",
});

onBeforeMount(async () => {
    let data = await apiGetDetails(props.id);
    Object.keys(ajaxParams).forEach((item: string) => {
        ajaxParams[item] = data[item];
    });
});
</script>

<template>
    <GlobalElDialog :="$attrs" @close="$emit('close')">
        <GlobalElForm ref="refElForm" label-width="5vw">
            <GlobalElFormItem label="参数键名" prop="configKey">
                {{ ajaxParams.configKey }}
            </GlobalElFormItem>

            <GlobalElFormItem label="参数名称" prop="configName">
                {{ ajaxParams.configName }}
            </GlobalElFormItem>

            <GlobalElFormItem label="系统内置" prop="configType">
                {{ ajaxParams.configType }}
            </GlobalElFormItem>

            <GlobalElFormItem label="参数键值" prop="configValue">
                {{ ajaxParams.configValue == '0' ? '关闭' : '开启' }}
            </GlobalElFormItem>

            <GlobalElFormItem label="备注" prop="remarks">
                {{ ajaxParams.remarks }}
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
