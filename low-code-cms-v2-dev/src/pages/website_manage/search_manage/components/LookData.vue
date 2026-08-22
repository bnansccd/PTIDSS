<script setup lang="ts">
import { reactive, onBeforeMount, ref } from "vue";

import { apiGetDetails } from "./api";
import type {
    UploadProps,
    UploadUserFile,
    UploadFile,
    UploadFiles,
} from "element-plus";
const props = defineProps<{
    id: string;
}>();

const fileList = ref<UploadUserFile[]>([]);
const ajaxParams = reactive({
    url: "", // 图片列表
    status: "", // 是否启用 0未 1启
    keyword: "", // 文章标题
});
onBeforeMount(async () => {
    const data = await apiGetDetails(props.id);
    Object.keys(ajaxParams).forEach((item: string) => {
        ajaxParams[item] = data[item];
    });
});
</script>

<template>
    <GlobalElDialog
        title="查看"
        width="50vw"
        :="$attrs"
        @close="$emit('close')"
    >
        <GlobalElForm
            ref="refElForm"
            size="large"
            label-position="right"
            label-width="5vw"
        >
            <GlobalElFormItem label="关键字" prop="keyword">
                {{ ajaxParams.keyword }}
            </GlobalElFormItem>

            <GlobalElFormItem label="前端地址" prop="url">
                {{ ajaxParams.url }}
            </GlobalElFormItem>

            <GlobalElFormItem label="是否启用" prop="status">
                <GlobalDictSelect
                    parentId="ENABLE"
                    v-model="ajaxParams.status"
                    placeholder="请选择是否启用"
                    type="text"
                    :id="ajaxParams.status"
                />
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
