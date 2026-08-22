<script setup lang="ts">
import { reactive, ref, onBeforeMount, computed } from "vue";
import type { FormInstance } from "element-plus";

import { apiPostAdd, apiGetDetails, apiPutEdit, apiGetSort } from "./api";
import { rules } from "./rules";
import { Search, Setting } from "@element-plus/icons-vue";
import ParentTreeSelect from "./components/ParentTreeSelect.vue";
import DictElradio from "./components/DictElradio.vue";

import * as iconModule from "@vicons/carbon";
const props = defineProps<{
    id: string;
    parentId: string;
    currentAppId: string;
}>();

const showIcon = ref(false);
const ajaxParams = reactive({
    href: "", // 路由地址
    icon: "",
    // isShow: "",
    isBase: "", // 基础菜单
    menuCode: "", // 权限编码
    menuName: "", // 菜单名称
    parentId: props.parentId,
    menuType: "",
    sort: "",
    appId: props.currentAppId,
    // status: "",
});

const iconList = Object.values(iconModule);

onBeforeMount(async () => {
    if (props.id !== "0") {
        const data = await apiGetDetails(props.id);
        Object.keys(ajaxParams).forEach((item: string) => {
            ajaxParams[item] = data[item];
        });
    } else {
        // 查询排序序号
        const sort = await apiGetSort(props.parentId);
        ajaxParams.sort = sort;
    }
});

const emit = defineEmits(["query", "close"]);

const refElForm = ref();
function submit(refElForm: FormInstance | null) {
    if (!refElForm) return;
    refElForm.validate(async (valid, _fields) => {
        if (valid) {
            const bool =
                props.id === "0"
                    ? await apiPostAdd(ajaxParams)
                    : await apiPutEdit(ajaxParams, props.id);
            bool && emit("close");
            bool && emit("query");
        }
    });
}

const iconObj = computed(() => {
    if (ajaxParams.icon) {
        return iconModule[ajaxParams.icon];
    }
    return {};
});

const onChoose = (item: any) => {
    ajaxParams.icon = item.name;
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
            <GlobalElFormItem label="父级菜单" prop="parentId">
                <ParentTreeSelect
                    v-model="ajaxParams.parentId"
                    placeholder="请输入父级菜单"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="菜单名称" prop="menuName">
                <el-input
                    v-model="ajaxParams.menuName"
                    placeholder="请输入菜单名称"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="路由地址" prop="href">
                <el-input
                    v-model="ajaxParams.href"
                    placeholder="请输入菜单名称"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="权限编码" prop="menuCode">
                <el-input
                    v-model="ajaxParams.menuCode"
                    placeholder="请输入权限code"
                />
            </GlobalElFormItem>

            <GlobalElFormItem label="图标" prop="icon">
                <div style="width: 100%; display: flex">
                    <component
                        :is="iconObj"
                        style="width: 20px; padding: 0 10px"
                    />
                    <el-input
                        style="flex: 1"
                        v-model="ajaxParams.icon"
                        disabled
                        placeholder="请选择图标"
                    >
                        <template #append>
                            <el-button
                                @click="showIcon = true"
                                :icon="Setting"
                            />
                        </template>
                    </el-input>
                </div>
            </GlobalElFormItem>

            <GlobalElFormItem label="菜单类型" prop="menuType">
                <GlobalDictSelect
                    parentId="MENU_TYPE"
                    placeholder="请选择菜单类型"
                    v-model="ajaxParams.menuType"
                />
            </GlobalElFormItem>
            <GlobalElFormItem label="基础菜单" prop="isBase">
                <GlobalDictSelect
                    parentId="TRUE_FALSE"
                    placeholder="请选择菜单类型"
                    v-model="ajaxParams.isBase"
                />
            </GlobalElFormItem>

            <!-- <GlobalElFormItem label="是否显示" prop="isShow">
                <el-radio-group v-model="ajaxParams.isShow">
                    <el-radio label="0">显示</el-radio>
                    <el-radio label="1">隐藏</el-radio>
                </el-radio-group>
            </GlobalElFormItem> -->

            <!-- <GlobalElFormItem label="启用停用" prop="status">
                <el-radio-group v-model="ajaxParams.status">
                    <el-radio label="0">启用</el-radio>
                    <el-radio label="1">停用</el-radio>
                </el-radio-group>
            </GlobalElFormItem> -->

            <GlobalElFormItem label="排序" prop="sort">
                <el-input
                    type="number"
                    v-model.number="ajaxParams.sort"
                    placeholder="请输入排序序号"
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

    <GlobalElDialog
        width="80%"
        v-model="showIcon"
        title="图标库"
        @close="showIcon = false"
    >
        <div class="icon-list-box">
            <div v-for="(item, index) in iconList" :key="index">
                <component
                    :is="item"
                    class="icon-box"
                    @click="onChoose(item)"
                ></component>
            </div>
        </div>
        <template #footer>
            <el-button type="primary" @click="showIcon = false">保存</el-button>

            <el-button type="info" @click="showIcon = false">取消</el-button>
        </template>
    </GlobalElDialog>
</template>

<style lang="scss" scoped>
.icon-list-box {
    width: calc(100% - 100px);
    padding: 0 50px;
    height: 600px;
    overflow-y: scroll;
    display: flex;
    flex-wrap: wrap;

    .icon-box {
        width: 25px;
        height: 25px;
        margin: 10px;
        padding: 8px;
        cursor: pointer;
    }
    .icon-box:hover {
        padding: 8px;
        background: rgb(228, 225, 225);
        border-radius: 6px;
    }
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "数据操作",
    name: "OperatData",
});
</script>
