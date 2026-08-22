<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";

import { apiPostMenu, apiGetDetails } from "./api";
import { apiGetListTree } from "@/pages/auth_manage/menu_manage/api";
import { rules } from "./rules";

const options = ref<any>([]);

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    menuIds: "",
});

onBeforeMount(async () => {
    const data = await apiGetDetails(props.id);
    ajaxParams.menuIds = data.menuIds;
    options.value = await apiGetListTree();
});

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
function submit(refElForm: FormInstance | null) {
    if (!refElForm) return;
    refElForm.validate(async (valid, fields) => {
        if (valid) {
            const bool = await apiPostMenu(ajaxParams, props.id);
            bool && emit("close");
            bool && emit("query");
        }
    });
}
</script>

<template>
    <GlobalElDialog title="配置角色菜单" :="$attrs" @close="emit('close')">
        <GlobalElForm
            ref="refElForm"
            label-width="5vw"
            :model="ajaxParams"
            :rules="rules"
        >
            <GlobalElFormItem label="菜单：" prop="dataRange">
                <el-tree-select
                    v-model="ajaxParams.menuIds"
                    :data="options"
                    multiple
                    :check-strictly="true"
                    :render-after-expand="false"
                    default-expand-all
                    show-checkbox
                    node-key="id"
                    :props="{
                        label: 'menuName',
                    }"
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

<style lang="scss" scoped></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "配置角色菜单",
    name: "ConfigRoleMenu",
});
</script>
