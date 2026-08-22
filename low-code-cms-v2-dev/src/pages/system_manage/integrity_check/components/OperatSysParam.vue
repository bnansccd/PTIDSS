<script setup lang="ts">
import { reactive, ref, onBeforeMount, toRaw } from "vue";
import type { FormInstance } from "element-plus";
import type { ConfigParams } from "@/types/index";
import { apiPostAdd, apiGetDetails, apiPutEdit } from "./api";
import { rules } from "./rules";

const props = defineProps<{
    id: string;
}>();

const ajaxParams = ref<Array<ConfigParams>>([]);

onBeforeMount(async () => {
    if (props.id !== "0") {
        const data = await apiGetDetails(props.id);
        ajaxParams.value = data;
        // Object.keys(ajaxParams).forEach((item: string) => {
        //     ajaxParams[item] = data[item];
        // });
    }
});

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
const submit = (refElForm: FormInstance | null) => {
    refElForm?.validate(async (valid, fields) => {
        if (valid) {
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
            <div v-for="(item, index) in ajaxParams" :key="index">
                <GlobalElFormItem
                    :label="item.configName"
                    :prop="item.configKey"
                >
                    <GlobalElSwitch
                        v-model="ajaxParams[index].configValue"
                        active-value="1"
                        inactive-value="0"
                    />
                </GlobalElFormItem>
            </div>
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
    title: "编辑系统参数",
    name: "OperatSysParam",
});
</script>
