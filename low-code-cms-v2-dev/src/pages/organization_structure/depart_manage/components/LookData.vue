<script setup lang="ts">
import { reactive, onBeforeMount } from "vue";

import { apiGetDetails } from "./api";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    departName: "",
    parentId: null,
    sort: null,
    userId: null,
    userName: "",
    phone: "",
    parent: {
        departName: "",
    },
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
            <GlobalElFormItem
                v-if="ajaxParams.parent"
                label="上级组织"
                prop="parentId"
            >
                {{ ajaxParams.parent && ajaxParams.parent.departName }}
            </GlobalElFormItem>

            <GlobalElFormItem label="组织名称" prop="departName">
                {{ ajaxParams.departName }}
            </GlobalElFormItem>
            <GlobalElFormItem label="组织负责人" prop="userName">
                {{ ajaxParams.userName }}
            </GlobalElFormItem>
            <GlobalElFormItem label="联系方式" prop="phone">
                {{ ajaxParams.phone }}
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
