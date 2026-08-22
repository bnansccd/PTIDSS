<script setup lang="ts">
import { ref } from "vue";
import type { FormInstance } from "element-plus";
import type { ConfigParams } from "@/types/index";
import { apiGetDetails, apiPutEdit } from "./api";
import { rules } from "./rules";

const props = defineProps<{
    id: string;
    row: ConfigParams;
}>();

const initParams: ConfigParams = {
    code: "color",
    codeName: "",
    configKey: "system_theme_color",
    configName: "系统主题色",
    configType: "1",
    configValue: "7",
    createTime: "2023-03-30 11:33:17",
    id: "1641282418350673922",
    list: null,
    modifyTime: "2023-04-06 01:49:53",
    remarks: null,
};

const ajaxParams = ref<Array<ConfigParams>>([initParams]);

const initData = async () => {
    if (props.id !== "0") {
        // const data = await apiGetDetails(props.id);
        ajaxParams.value = [props.row];
    }
};

initData();

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
const submit = (refElForm: FormInstance | null) => {
    refElForm?.validate(async (valid, fields) => {
        if (valid) {
            const bool = await apiPutEdit(ajaxParams.value[0]);
            bool && emit("close");
            bool && emit("query");
        }
    });
};

const onChoose = (index: string) => {
    ajaxParams.value[0].configValue = index;
};
</script>

<template>
    <GlobalElDialog :="$attrs" width="60vw" @close="emit('close')">
        <GlobalElForm
            ref="refElForm"
            label-width="5vw"
            :model="ajaxParams"
            :rules="rules"
        >
            <GlobalElFormItem label="系统主题色" prop="">
                <div class="color-box">
                    <div
                        class="color-item-1"
                        @click="onChoose('1')"
                        :class="{
                            'active-item':
                                ajaxParams[0].configValue == '1' ? true : false,
                        }"
                    >
                        <div
                            class="active-box"
                            v-if="ajaxParams[0].configValue == '1'"
                        >
                            <el-icon :size="25" class="active-box-icon"
                                ><Check
                            /></el-icon>
                        </div>
                    </div>
                    <div
                        class="color-item-2"
                        @click="onChoose('2')"
                        :class="{
                            'active-item':
                                ajaxParams[0].configValue == '2' ? true : false,
                        }"
                    >
                        <div
                            class="active-box"
                            v-if="ajaxParams[0].configValue == '2'"
                        >
                            <el-icon :size="25" class="active-box-icon"
                                ><Check
                            /></el-icon>
                        </div>
                    </div>
                    <div
                        class="color-item-3"
                        @click="onChoose('3')"
                        :class="{
                            'active-item':
                                ajaxParams[0].configValue == '3' ? true : false,
                        }"
                    >
                        <div
                            class="active-box"
                            v-if="ajaxParams[0].configValue == '3'"
                        >
                            <el-icon :size="25" class="active-box-icon"
                                ><Check
                            /></el-icon>
                        </div>
                    </div>
                    <div
                        class="color-item-4"
                        @click="onChoose('4')"
                        :class="{
                            'active-item':
                                ajaxParams[0].configValue == '4' ? true : false,
                        }"
                    >
                        <div
                            class="active-box"
                            v-if="ajaxParams[0].configValue == '4'"
                        >
                            <el-icon :size="25" class="active-box-icon"
                                ><Check
                            /></el-icon>
                        </div>
                    </div>
                    <div
                        class="color-item-5"
                        @click="onChoose('5')"
                        :class="{
                            'active-item':
                                ajaxParams[0].configValue == '5' ? true : false,
                        }"
                    >
                        <div
                            class="active-box"
                            v-if="ajaxParams[0].configValue == '5'"
                        >
                            <el-icon :size="25" class="active-box-icon"
                                ><Check
                            /></el-icon>
                        </div>
                    </div>
                    <div
                        class="color-item-6"
                        @click="onChoose('6')"
                        :class="{
                            'active-item':
                                ajaxParams[0].configValue == '6' ? true : false,
                        }"
                    >
                        <div
                            class="active-box"
                            v-if="ajaxParams[0].configValue == '6'"
                        >
                            <el-icon :size="25" class="active-box-icon"
                                ><Check
                            /></el-icon>
                        </div>
                    </div>
                    <div
                        class="color-item-7"
                        @click="onChoose('7')"
                        :class="{
                            'active-item':
                                ajaxParams[0].configValue == '7' ? true : false,
                        }"
                    >
                        <div
                            class="active-box"
                            v-if="ajaxParams[0].configValue == '7'"
                        >
                            <el-icon :size="25" class="active-box-icon"
                                ><Check
                            /></el-icon>
                        </div>
                    </div>
                    <div
                        class="color-item-8"
                        @click="onChoose('8')"
                        :class="{
                            'active-item':
                                ajaxParams[0].configValue == '8' ? true : false,
                        }"
                    >
                        <div
                            class="active-box"
                            v-if="ajaxParams[0].configValue == '8'"
                        >
                            <el-icon :size="25" class="active-box-icon"
                                ><Check
                            /></el-icon>
                        </div>
                    </div>
                </div>
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

<style lang="scss" scoped>
.color-box {
    display: flex;
    align-items: center;
    flex-wrap: wrap;

    .color-item-1 {
        width: 300px;
        height: 150px;
        background: url("./imgs/theme1.png");
        background-size: 100% 100%;
        border-radius: 12px;
        margin: 10px;
        cursor: pointer;
        border: 1px solid var(--sys-theme-table-row-hover);
        overflow: hidden;
        position: relative;
    }
    .color-item-2 {
        width: 300px;
        height: 150px;
        background: url("./imgs/theme2.png");
        background-size: 100% 100%;
        border-radius: 12px;
        margin: 10px;
        cursor: pointer;
        border: 1px solid var(--sys-theme-table-row-hover);
        position: relative;
        overflow: hidden;
    }
    .color-item-3 {
        width: 300px;
        height: 150px;
        background: url("./imgs/theme3.png");
        background-size: 100% 100%;
        border-radius: 12px;
        margin: 10px;
        cursor: pointer;
        border: 1px solid var(--sys-theme-table-row-hover);
        position: relative;
        overflow: hidden;
    }
    .color-item-4 {
        width: 300px;
        height: 150px;
        background: url("./imgs/theme4.png");
        background-size: 100% 100%;
        border-radius: 12px;
        margin: 10px;
        cursor: pointer;
        border: 1px solid var(--sys-theme-table-row-hover);
        position: relative;
        overflow: hidden;
    }
    .color-item-5 {
        width: 300px;
        height: 150px;
        background: url("./imgs/theme5.png");
        background-size: 100% 100%;
        border-radius: 12px;
        margin: 10px;
        cursor: pointer;
        border: 1px solid var(--sys-theme-table-row-hover);
        position: relative;
        overflow: hidden;
    }
    .color-item-6 {
        width: 300px;
        height: 150px;
        background: url("./imgs/theme6.png");
        background-size: 100% 100%;
        border-radius: 12px;
        margin: 10px;
        cursor: pointer;
        border: 1px solid var(--sys-theme-table-row-hover);
        position: relative;
        overflow: hidden;
    }
    .color-item-7 {
        width: 300px;
        height: 150px;
        background: url("./imgs/theme7.png");
        background-size: 100% 100%;
        border-radius: 12px;
        margin: 10px;
        cursor: pointer;
        border: 1px solid var(--sys-theme-table-row-hover);
        position: relative;
        overflow: hidden;
    }

    .color-item-8 {
        width: 300px;
        height: 150px;
        background: url("./imgs/theme8.png");
        background-size: 100% 100%;
        border-radius: 12px;
        margin: 10px;
        cursor: pointer;
        border: 1px solid var(--sys-theme-table-row-hover);
        position: relative;
        overflow: hidden;
    }
    .active-item {
        border: 1px solid var(--sys-theme-btn-primary-border-color);
    }

    .active-box {
        width: 40px;
        height: 30px;
        background: var(--sys-theme-table-row-hover);
        position: absolute;
        right: -18px;
        top: -18px;
        border-radius: 50%;
        display: flex;
        padding-top: 15px;
        padding-left: 10px;

        .active-box-icon {
            color: var(--sys-theme-btn-primary-border-color);
            margin-left: -3px;
            margin-top: 2px;
        }
    }
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "编辑系统主题色",
    name: "OperatSysTheme",
});
</script>
