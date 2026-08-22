<script setup lang="ts">
import { reactive, onBeforeMount, ref, nextTick } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import type { TabsPaneContext } from "element-plus";
import { apiGetList, apiDelete, initParams } from "./api";
import BtnAuth from "./auth_btn";
import DataModel from "./AppDevelop/data_model/HomeView.vue";
import FormModel from "./AppDevelop/form_model/HomeView.vue";
import ProcessModel from "./AppDevelop/process_model/HomeView.vue";
import ReportModel from "./AppDevelop/report_model/HomeView.vue";
import PageModel from "./AppDevelop/page_model/HomeView.vue";
import DictModel from "./AppDevelop/dict_model/HomeView.vue";
import MenuModel from "./AppDevelop/menu_model/HomeView.vue";
const emits = defineEmits(["toBack"]);
const activeName = ref("dataModel");

const handleClick = (tab: TabsPaneContext, event: Event) => {
    console.log(tab, event);
};
const opInit = {
    show: false,
    code: "init", // "look" "edit" "add"
    id: "0", //id为0时表示添加
    roleName: "", // 角色名称
};
const opObject = reactive({
    ...opInit,
});
const ajaxParams = reactive({ ...initParams });
const tabList = ref([
    { tabLabel: "数据模型", tabName: "dataModel" },
    { tabLabel: "表单模型", tabName: "formModel" },
    { tabLabel: "流程模型", tabName: "processModel" },
    { tabLabel: "报表模型", tabName: "reportModel" },
    { tabLabel: "页面模型", tabName: "pageModel" },
    { tabLabel: "数据字典", tabName: "dictModel" },
    { tabLabel: "菜单配置", tabName: "menuModel" },
]);
const tabList2 = ref([
    { tabLabel: "实体模型", tabName: "st" },
    { tabLabel: "ER模型", tabName: "er" },
]);
const reset = () => {};
const getList = () => {};
const toBack = () => {
    emits("toBack");
};
</script>

<template>
    <WrapLayout>
        <div class="head-box">
            <el-icon
                @click="toBack"
                :size="20"
                style="cursor: pointer; margin-right: 8px"
                class="head-icon-back"
                ><Back
            /></el-icon>
            <el-breadcrumb separator="/">
                <el-breadcrumb-item @click="toBack" style="font-weight: bolder"
                    >应用管理</el-breadcrumb-item
                >
                <el-breadcrumb-item><a>应用开发</a></el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div>
            <el-tabs v-model="activeName" @tab-click="handleClick">
                <el-tab-pane
                    :label="item.tabLabel"
                    :name="item.tabName"
                    v-for="(item, index) in tabList"
                    :key="index"
                ></el-tab-pane>
            </el-tabs>
        </div>

        <div>
            <DataModel v-if="activeName == 'dataModel'" />
            <FormModel v-if="activeName == 'formModel'" />
            <ProcessModel v-if="activeName == 'processModel'" />
            <ReportModel v-if="activeName == 'reportModel'" />
            <PageModel v-if="activeName == 'pageModel'" />
            <DictModel v-if="activeName == 'dictModel'" />
            <MenuModel v-if="activeName == 'menuModel'" />
        </div>

        <template #drop> </template>
    </WrapLayout>
</template>

<style lang="scss" scoped>
.head-box {
    display: flex;
    align-items: center;

    .head-icon-back {
        color: var(--sys-theme-btn-plain-color);
    }
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "应用开发",
    name: "AppDevelop",
});
</script>
