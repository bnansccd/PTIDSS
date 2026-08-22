<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";

import { apiPutAuth, apiGetRoleDetails } from "./api";
import { rules } from "./rules";

import ParentDepartSelect from "@/pages/organization_structure/depart_manage/global/ParentDepartSelect.vue";
import DictSelect from "./components/DictSelect.vue";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = reactive({
    dataRange: "",
    // 数据范围 1=所有数据权限,2=自定义数据权限,3=本组织数据权限,4=本组织及以下数据权限,5=仅本人数据权限
    departIds: [], // 组织id  ===2
});

onBeforeMount(async () => {
    const data = await apiGetRoleDetails(props.id);
    ajaxParams.dataRange = data.dataRange;
    ajaxParams.departIds = data.departIds;
});

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
function submit(refElForm: FormInstance | null) {
    if (!refElForm) return;
    refElForm.validate(async (valid, fields) => {
        if (valid) {
            const bool: boolean | undefined = await apiPutAuth(
                ajaxParams,
                props.id
            );
            bool && emit("close");
            bool && emit("query");
        }
    });
}
</script>

<template>
    <GlobalElDialog title="配置数据权限" :="$attrs" @close="emit('close')">
        <GlobalElForm
            ref="refElForm"
            label-width="5vw"
            :model="ajaxParams"
            :rules="rules"
        >
            <GlobalElFormItem label="数据范围：" prop="dataRange">
                <DictSelect v-model="ajaxParams.dataRange" />
            </GlobalElFormItem>

            <GlobalElFormItem
                label="组织："
                prop="departIds"
                v-if="ajaxParams.dataRange === '2'"
            >
                <ParentDepartSelect
                    style="width: 100% !important"
                    multiple
                    v-model="ajaxParams.departIds"
                    placeholder="请选择组织"
                    check-strictly
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
    title: "配置数据权限",
    name: "ConfigDataAuth",
});
</script>
