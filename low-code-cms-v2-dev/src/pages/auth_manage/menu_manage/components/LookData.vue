<script setup lang="ts">
import { reactive, onBeforeMount } from "vue";

import { apiGetDetails } from "./api";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    href: "",
    icon: "",
    isShow: "0",
    menuCode: "",
    menuName: "",
    menuTypeName: "",
    menuType: "0",
    parentId: null,
    sort: null,
    status: "0",
    parent: {
        menuName: "",
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
    <GlobalElDialog title="查看" :="$attrs" @close="$emit('close')">
        <GlobalElForm ref="refElForm" label-width="5vw">
            <GlobalElFormItem
                v-show="ajaxParams.parent"
                label="父级菜单"
                prop="parentId"
            >
                {{ ajaxParams.parent && ajaxParams.parent.menuName }}
            </GlobalElFormItem>

            <GlobalElFormItem label="菜单名称" prop="menuName">
                {{ ajaxParams.menuName }}
            </GlobalElFormItem>

            <GlobalElFormItem label="权限code" prop="menuCode">
                {{ ajaxParams.menuCode }}
            </GlobalElFormItem>

            <GlobalElFormItem label="路由地址" prop="href">
                {{ ajaxParams.href }}
            </GlobalElFormItem>

            <GlobalElFormItem label="图标" prop="icon">
                {{ ajaxParams.icon }}
            </GlobalElFormItem>

            <GlobalElFormItem label="菜单类型" prop="menuType">
                {{ ajaxParams.menuTypeName }}
            </GlobalElFormItem>

            <GlobalElFormItem label="是否显示" prop="isShow">
                {{ ajaxParams.isShow === "0" ? "显示" : "隐藏" }}
            </GlobalElFormItem>

            <GlobalElFormItem label="启用停用" prop="status">
                {{ ajaxParams.status === "0" ? "启用" : "停用" }}
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
