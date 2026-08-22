<script setup lang="ts">
import { reactive, onBeforeMount, ref, nextTick } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import type { TabsPaneContext } from "element-plus";
import { apiGetList, apiDelete, initParams, apiReset, apiLook } from "./api";
import BtnAuth from "./auth_btn";
import OperatData from "./components/OperatData.vue";
import ConfigRoleMenuRight from "./components/ConfigRoleMenuRight.vue";
const emits = defineEmits(["onChose"]);
const activeName = ref("all");

const handleClick = (tab: TabsPaneContext, event: Event) => {
    console.log(tab, event);
};
const opInit = {
    show: false,
    code: "init", // "look" "edit" "add"
    id: "0", //id为0时表示添加
    parentId: "",
    appName: "",
};
const opObject = reactive({
    ...opInit,
});
const ajaxParams = reactive({ ...initParams });
const tabList = ref([
    { tabLabel: "全部", tabName: "all" },
    // { tabLabel: "办公类(6)", tabName: "first" },
    // { tabLabel: "人事类(1)", tabName: "second" },
    // { tabLabel: "办公类(6)", tabName: "three" },
    // { tabLabel: "办公类(6)", tabName: "four" },
]);
const response = reactive({
    records: [],
    total: 0,
});
const loading = ref<boolean>(false);
const reset = () => {
    Object.assign(ajaxParams, initParams);
    getList();
};
const getList = async () => {
    loading.value = true;
    const { total, records } = await apiGetList(ajaxParams);
    loading.value = false;
    response.records = records;
    response.total = total;

    tabList.value[0].tabLabel = `全部(${total})`;
};

onBeforeMount(() => {
    getList();
});
const close = () => /* 关闭模态框 */ {
    Object.assign(opObject, opInit);
};
const onChose = () => {
    emits("onChose");
};

const onConfig = (appObj: AppParams) => {
    opObject.id = appObj.id!;
    opObject.appName = appObj.name;
    opObject.show = true;
    opObject.code = "menu";
    // opObject.roleName = row.roleName;
};

const onReset = async (appObj: AppParams) => {
    ElMessageBox.confirm("确定重置秘钥?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        center: true,
    })
        .then(async () => {
            const bool = await apiReset(appObj.id!);
            bool && (ajaxParams.current = 1);
            getList();
        })
        .catch(() => {});
};

const onEdit = (appObj: AppParams) => {
    opObject.id = appObj.id!;
    opObject.show = true;
    opObject.code = "edit";
};
const onLook = async (appObj: AppParams) => {
    const data = await apiLook(appObj.id!);
    // ElMessageBox.confirm(`${data.secret}`, "秘钥", {
    //     confirmButtonText: "确定",
    //     cancelButtonText: "取消",
    //     type: "info",
    //     center: true,
    // });

    ElMessageBox.alert(
        `<div><span>appId: </span><span>${data.appId}</span></div><div><span>秘钥: </span><span>${data.secret}</span></div>`,
        "查看秘钥",
        {
            dangerouslyUseHTMLString: true,
        }
    );
};
const del = (appObj: AppParams) => {
    ElMessageBox.confirm("此操作将永久删除选择数据, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        center: true,
    })
        .then(async () => {
            const bool = await apiDelete(appObj.id!);
            bool && (ajaxParams.current = 1);
            getList();
        })
        .catch(() => {});
};
</script>

<template>
    <OperatData
        v-if="opObject.code === 'add' || opObject.code === 'edit'"
        :title="opObject.code === 'add' ? '添加' : '编辑'"
        :id="opObject.id"
        v-model="opObject.show"
        :parent-id="opObject.parentId"
        @query="
            () => {
                getList();
            }
        "
        @close="close"
    />
    <div class="role-manage-box">
        <WrapTableLayout
            class="left-box"
            @query="getList()"
            @reset="reset"
            v-loading="loading"
            more
        >
            <template #header>
                <GlobalSearchInput
                    @query="getList()"
                    v-model="ajaxParams.name"
                    placeholder="请输入关键字查询"
                    clearable
                    style="width: 200px"
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

            <div style="display: flex; flex-wrap: wrap">
                <div v-for="(item, index) in response.records" :key="index">
                    <AppCard
                        @onConfig="onConfig"
                        @onEdit="onEdit"
                        @onDel="del"
                        @onReset="onReset"
                        @onLook="onLook"
                        :appObj="item"
                    />
                </div>
            </div>

            <template #drop> </template>
        </WrapTableLayout>
        <div
            class="right-box"
            :style="{
                width:
                    opObject.code === 'menu' && opObject.show ? '320px' : '0px',
                margin:
                    opObject.code === 'menu' && opObject.show
                        ? '0 20px 20px 0'
                        : '0 0 20px 0',
            }"
        >
            <div
                class="close-box"
                v-if="opObject.code === 'menu' && opObject.show"
            >
                应用 : &nbsp;{{ opObject.appName }}
                <el-icon
                    :size="20"
                    style="cursor: pointer"
                    @click="opObject.show = false"
                    ><CircleClose color="#D1D3D5"
                /></el-icon>
            </div>
            <div class="right-content-box">
                <ConfigRoleMenuRight
                    v-if="opObject.code === 'menu' && opObject.show"
                    :id="opObject.id"
                    :app-name="opObject.appName"
                    @query="getList()"
                    @close="
                        () => {
                            opObject.show = false;
                        }
                    "
                />
            </div>
        </div>
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

    .right-box {
        background: var(--sys-theme-warp-background);
        margin: 0 30px 20px 0;

        border-radius: 12px;
        transition: width 0.5s;
        color: var(--sys-theme-form-label-color);

        :deep(.el-tree) {
            background: var(--sys-theme-warp-background);
        }
        :deep(.el-tree-node__content:hover) {
            background: var(--sys-theme-table-row-hover);
        }

        .close-box {
            width: calc(100% - 40px);
            text-align: right;
            padding: 20px 0px 0 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .right-content-box {
            width: calc(100% - 0px);
            //  padding: 10px;
            height: 100%;
            // height: calc(100vh - 200px);
            // overflow-y: auto;
        }
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
import type { AppParams } from "./components/api";
export default defineComponent({
    title: "应用管理",
    name: "AppManage",
    components: { AppCard },
});
</script>
