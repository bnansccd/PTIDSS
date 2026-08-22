<script setup lang="ts">
import { reactive, onBeforeMount, ref, nextTick } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import type { TabsPaneContext } from "element-plus";
import { apiGetList, apiDelete, initParams } from "./api";
import BtnAuth from "./auth_btn";
const emits = defineEmits(["onChose"]);
const activeName = ref("first");

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
    { tabLabel: "全部(20)", tabName: "all" },
    { tabLabel: "办公类(6)", tabName: "first" },
    { tabLabel: "人事类(1)", tabName: "second" },
    { tabLabel: "办公类(6)", tabName: "three" },
    { tabLabel: "办公类(6)", tabName: "four" },
]);
const reset = () => {};
const getList = () => {};

const onChose = () => {
    emits("onChose");
};
</script>

<template>
    <div class="role-manage-box">
        <WrapTableLayout
            class="left-box"
            @query="getList()"
            @reset="reset"
            more
        >
            <template #header>
                <GlobalSearchInput
                    @query="getList()"
                    v-model="ajaxParams.roleName"
                    placeholder="请输入关键字查询"
                ></GlobalSearchInput>
            </template>

            <template #toolbar>
                <div class="tool-bar-box">
                    <GlobalAuthElButton
                        :auth="BtnAuth.add"
                        type="primary"
                        @click="
                            opObject.show = true;
                            opObject.code = 'add';
                        "
                        >新增应用
                    </GlobalAuthElButton>
                </div>
            </template>

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

            <div style="display: flex">
                <AppCard @onChose="onChose" />
                <AppCard @onChose="onChose" />
                <AppCard @onChose="onChose" />
                <AppCard @onChose="onChose" />
                <AppCard @onChose="onChose" />
            </div>

            <template #drop> </template>
        </WrapTableLayout>
    </div>
</template>

<style lang="scss" scoped>
.role-manage-box {
    width: 100%;
    height: 100%;
    display: flex;
    overflow: hidden;
    .left-box {
        flex: 1;
        height: calc(100% - 60px) !important;
        overflow: hidden;
        padding: 20px;
        display: flex;
        flex-direction: column;
        background: var(--sys-theme-warp-background);
        border-radius: 12px 12px 12px 12px;
        margin: 0px 20px 0 20px;
    }
}

.vxe-table-row {
    background-color: red !important;
}

.tool-bar-box {
    margin: 10px 0;
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
import AppCard from "./components/AppCard.vue";
export default defineComponent({
    title: "应用管理",
    name: "AppManage",
    components: { AppCard },
});
</script>
