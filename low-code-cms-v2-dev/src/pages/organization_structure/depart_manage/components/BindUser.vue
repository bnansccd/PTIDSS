<template>
     <GlobalElDialog :="$attrs" @close="emit('close')">
        <GlobalElForm
            ref="refElForm"
            label-width="6vw"
            :model="ajaxParams"
            :rules="[]"
        >
            <GlobalElFormItem label="选择同步时间" prop="departId">
                <el-date-picker
                    v-model="ajaxParams.date"
                    type="date"
                    placeholder="选择时间"
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
<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";
import type { Response } from "@/types/index";
import $api from "@/api/Axios";
import {
    importOrg
} from "../api";
const emit = defineEmits(["query", "close"]);
const ajaxParams:any = reactive({
    date: ''
})
const loading = ref(false)
const refElForm = ref();
const getDate = async () => {
    loading.value = true
    const response: Response = await $api.get(`/system/api/web/v1/sso/syncOrg/lastTime`);
    const { code, data } = response;
    if (code === 200) {
        console.log(data)
        data && (ajaxParams.date = new Date(Number(data)))
    }
    loading.value = false
}
getDate()
const isSync = ref(false)
const submit = (refElForm: FormInstance | null) => {
    if (!refElForm) return;
    refElForm.validate(async (valid, fields) => {
        if (valid) {
            // const params = JSON.parse(JSON.stringify(ajaxParams));
            let date = ''
            if (ajaxParams.date) {
                date = (ajaxParams.date as any).getTime()
            }
            isSync.value = true
            const bool = await importOrg(date)
            isSync.value = false
            bool && emit("close");
            bool && emit("query");
        }
    });
}
</script>
