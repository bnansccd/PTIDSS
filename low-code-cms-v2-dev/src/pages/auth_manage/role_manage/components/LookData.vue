<script setup lang="ts">
import { reactive, onBeforeMount } from "vue";

import { apiGetDetails } from "./api";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    roleCode: "",
    roleName: "",
    sort: null,
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
            <GlobalElFormItem label="角色编码" prop="roleCode">
                {{ ajaxParams.roleCode }}
            </GlobalElFormItem>

            <GlobalElFormItem label="角色名称" prop="roleName">
                {{ ajaxParams.roleName }}
            </GlobalElFormItem>

            <GlobalElFormItem label="排序" prop="sort">
                {{ ajaxParams.sort }}
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
