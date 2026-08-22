<script setup lang="ts">
import { reactive, ref, onBeforeMount, watch } from "vue";
import type { FormInstance } from "element-plus";
import { ElMessage } from "element-plus";

import { apiBindApp, apiUpdateApp } from "./api";
import { appRules } from "./rules";
import AppSelect from "@/pages/system_manage/app_manage/global/AppSelect.vue";

const props = defineProps<{
    id: string;
    tenantId: string;
    show: boolean;
    appId?: string;
    status?: string;
    validStartTime?: string;
    validEndTime?: string;
}>();

const ajaxParams = reactive({
    appId: "", // 应用ID
    status: "", // 状态（0：停用；1：正常）
    tenantId: props.tenantId, // 租户ID
    validEndTime: "", // 有效期结束时间（格式：YYYY-MM-DD HH:mm:ss）
    validStartTime: "", // 有效期开始时间（格式：YYYY-MM-DD HH:mm:ss）
});

const loading = ref(false);

// 监听show属性变化，初始化表单数据
watch(
    () => props.show,
    (newVal) => {
        if (newVal) {
            // 编辑模式：如果传入了appId，则使用传入的数据
            if (props.appId) {
                ajaxParams.appId = props.appId;
                ajaxParams.tenantId = props.tenantId;
                ajaxParams.status = props.status || "";
                ajaxParams.validStartTime = props.validStartTime || "";
                ajaxParams.validEndTime = props.validEndTime || "";
            } else {
                // 新增模式：重置表单
                resetForm();
            }
        }
    },
    { immediate: true }
);

const emit = defineEmits<{
    "update:show": [value: boolean];
    query: [];
    close: [];
}>();

const refElForm = ref<FormInstance | null>(null);

// 重置表单
function resetForm() {
    ajaxParams.appId = "";
    ajaxParams.status = "";
    ajaxParams.validStartTime = "";
    ajaxParams.validEndTime = "";
    refElForm.value?.resetFields();
}

function submit() {
    if (!refElForm.value) return;
    refElForm.value.validate(async (valid, fields) => {
        if (valid) {
            // 结束时间要大于开始时间
            if (
                ajaxParams.validEndTime &&
                ajaxParams.validStartTime &&
                ajaxParams.validEndTime <= ajaxParams.validStartTime
            ) {
                ElMessage.warning("有效期结束时间必须晚于开始时间");
                return;
            }

            // 根据是否有appId判断是新增还是更新
            let bool;
            if (props.appId) {
                // 更新操作：创建不包含appId的更新参数（避免重复）
                loading.value = true;
                const updateParams = { ...ajaxParams };
                bool = await apiUpdateApp(props.id, updateParams);
                loading.value = false;
            } else {
                // 新增操作：直接调用绑定API
                loading.value = true;
                bool = await apiBindApp(ajaxParams);
                loading.value = false;
            }

            if (bool) {
                emit("update:show", false);
                emit("close");
                emit("query");
            }
        }
    });
}

// 关闭弹窗
function close() {
    emit("update:show", false);
    emit("close");
}
</script>

<template>
    <GlobalElDialog
        width="700px"
        :model-value="props.show"
        :="$attrs"
        @close="close"
        @update:model-value="emit('update:show', $event)"
    >
        <GlobalElForm
            ref="refElForm"
            label-width="7vw"
            :model="ajaxParams"
            :rules="appRules"
        >
            <GlobalElFormItem label="应用" prop="appId">
                <AppSelect
                    v-model="ajaxParams.appId"
                    placeholder="请选择应用"
                    :disabled="!!props.appId"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="状态" prop="status">
                <GlobalElSwitch
                    v-model="ajaxParams.status"
                    active-value="1"
                    inactive-value="0"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="有效期开始时间" prop="validStartTime">
                <el-date-picker
                    v-model="ajaxParams.validStartTime"
                    type="datetime"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    placeholder="请选择有效期开始时间"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="有效期结束时间" prop="validEndTime">
                <el-date-picker
                    v-model="ajaxParams.validEndTime"
                    type="datetime"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    placeholder="请选择有效期结束时间"
                />
            </GlobalElFormItem>
        </GlobalElForm>

        <template #footer>
            <el-button type="primary" :loading="loading" @click="submit"
                >保存</el-button
            >

            <el-button @click="close">取消</el-button>
        </template>
    </GlobalElDialog>
</template>

<style scoped></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "应用管理",
    name: "OperatApp",
});
</script>
