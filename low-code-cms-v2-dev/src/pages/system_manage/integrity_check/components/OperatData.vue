<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import type { FormInstance } from "element-plus";

// import { apiPostAdd, apiGetDetails, apiPutEdit } from "./api";
import { apiValid } from "../api";
import { rules } from "./rules";

const props = defineProps<{
    id: string | number;
}>();
const ajaxParams = reactive({
    name: "",
});

const selectList = [
    { name: "用户", val: "1" },
    { name: "usbKey", val: "2" },
    { name: "角色", val: "3" },
    { name: "登录日志", val: "4" },
    { name: "操作日志", val: "5" },
    { name: "系统配置", val: "6" },
    { name: "从业人员信息", val: "7" },
    { name: "运营车辆信息", val: "8" },
    { name: "业户信息", val: "9" },
    { name: "航道", val: "10" },
    { name: "拦截线", val: "11" },
    { name: "船闸", val: "12" },
    { name: "停泊区", val: "13" },
];
onBeforeMount(async () => {});

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
const submit = async (refElForm: FormInstance | null) => {
    const getUrl = () => {
        // /api/web/v1/check/1
        const option: any = selectList.find(
            (item: any) => item.name === ajaxParams.name
        );
        const val = Number(option.val);
        if (!val) {
            //
            return false;
        }
        // if ([1,2,3,4,5,6].includes(val)) {
        //     return `/system/api/web/v1/check/${val}`
        // } else if (val == 10) {
        //     return `/basics/api/web/v1/check/${val}`
        // } else if ([7,8,9,11,12,13].includes(val)) {
        //     return `/traffic/api/web/v1/check/${val}`
        // }

        // 统一改成这个接口
        return `/system/api/web/v1/check/${val}`;
    };
    const url = getUrl();
    const bool = await apiValid(url);
    bool && emit("close");
    bool && emit("query");
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
            <GlobalElFormItem label="校验类型" prop="name">
                <el-select v-model="ajaxParams.name" clearable>
                    <el-option
                        v-for="item in selectList"
                        :key="item.val"
                        :label="item.name"
                        :value="item.name"
                    >
                    </el-option>
                </el-select>
            </GlobalElFormItem>
        </GlobalElForm>

        <template #footer>
            <el-button type="primary" @click="submit(refElForm)"
                >校验</el-button
            >

            <el-button @click="$emit('close')">取消</el-button>
        </template>
    </GlobalElDialog>
</template>

<style scoped></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "数据操作",
    name: "OperatData",
});
</script>
