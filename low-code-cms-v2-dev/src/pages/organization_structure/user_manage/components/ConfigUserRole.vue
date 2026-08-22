<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";

import { apiPostMenu, apiGetDetails } from "./api";
import { rules } from "./rules";

import RoleSelect from "@/pages/auth_manage/role_manage/global/RoleSelect.vue";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    roleIds: [],
    sysRoleVOS: [],
});
onBeforeMount(async () => {
    let data = await apiGetDetails(props.id);
    ajaxParams.roleIds = data.sysRoleVOS.map((item: any) => item.id);
});

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
function submit(refElForm: FormInstance | null) {
    if (!refElForm) return;
    refElForm.validate(async (valid, fields) => {
        if (valid) {
            let bool: boolean | undefined = await apiPostMenu(
                props.id,
                ajaxParams.roleIds
            );
            bool && emit("close");
            bool && emit("query");
        }
    });
}
</script>

<template>
    <GlobalElDialog title="配置用户角色" :="$attrs" @close="emit('close')">
        <el-form
            ref="refElForm"
            label-width="5vw"
            :model="ajaxParams"
            :rules="rules"
        >
            <GlobalElFormItem label="角色：" prop="dataRange">
                <RoleSelect v-model="ajaxParams.roleIds" />
            </GlobalElFormItem>
        </el-form>

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
    title: "配置用户角色",
    name: "ConfigUserRole",
});
</script>
