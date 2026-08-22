<script setup lang="ts">
import { reactive, ref, watch, onBeforeMount, toRaw } from "vue";
import type { FormInstance } from "element-plus";
import type { ConfigParams } from "@/types/index";
import { apiPostAdd, apiGetDetails, apiPutEdit } from "./api";
import { rules } from "./rules";

const props = defineProps<{
    id: string;
    row: ConfigParams;
}>();

const ajaxParams = ref<ConfigParams>(props.row);

watch(
    () => props.row,
    (newVal: ConfigParams) => {
        console.log(newVal);
        ajaxParams.value = newVal;
    }
);

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
const submit = (refElForm: FormInstance | null) => {
    refElForm?.validate(async (valid, fields) => {
        if (valid) {
            // const ajaxParamsCurrent: ConfigParams = {
            //     configKey: ajaxParams.value.configKey,
            //     configName: ajaxParams.value.configName,
            //     configValue: ajaxParams.value.configValue,
            // };
            const bool = await apiPutEdit(ajaxParams.value);
            bool && emit("close");
            bool && emit("query");
        }
    });
};
</script>

<template>
    <GlobalElDialog :="$attrs" @close="emit('close')">
        <GlobalElForm
            ref="refElForm"
            label-width="5vw"
            :model="ajaxParams"
            :rules="rules"
        >
            <GlobalElFormItem
                :label="ajaxParams.configName"
                :prop="ajaxParams.configKey"
                v-if="ajaxParams.configKey == 'system_titile_color'"
            >
                <el-color-picker
                    v-model="ajaxParams.configValue"
                    show-alpha
                    color-format="hex"
                />
            </GlobalElFormItem>

            <GlobalElFormItem
                v-else-if="
                    ajaxParams.configKey == 'system_bread' ||
                    ajaxParams.configKey == 'system_label'
                "
                :label="ajaxParams.configName"
                :prop="ajaxParams.configKey"
            >
                <GlobalElSwitch
                    v-model="ajaxParams.configValue"
                    active-value="1"
                    inactive-value="0"
                />
            </GlobalElFormItem>
            <GlobalElFormItem
                :label="ajaxParams.configName"
                :prop="ajaxParams.configKey"
                v-else
            >
                <el-input
                    v-model="ajaxParams.configValue"
                    placeholder="请输入参数键名"
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
    title: "编辑系统标题",
    name: "OperatTitle",
});
</script>
