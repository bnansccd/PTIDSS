<script setup lang="ts">
import { reactive, ref, onBeforeMount } from "vue";
import { ElMessageBox } from "element-plus";

import { apiGetAppList, apiUnBindApp } from "./api";
import OperatApp from "./OperatApp.vue";
import ConfigRoleMenuRight from "./ConfigRoleMenuRight.vue";

const props = defineProps<{
    id: string;
}>();

const response = reactive({
    appList: [],
    loading: false,
});

// 编辑应用状态
const editApp = reactive({
    show: false,
    id: "",
    appId: "",
    status: "",
    validStartTime: "",
    validEndTime: "",
});

// 新增应用状态
const addApp = reactive({
    show: false,
});

const configRoleMenuRight = reactive({
    show: false,
    appId: "",
    appName: "",
});

// 打开新增应用弹窗
const openAddApp = () => {
    addApp.show = true;
};

const openConfigRoleMenuRight = (row: any) => {
    configRoleMenuRight.show = true;
    configRoleMenuRight.appId = row.appId;
    configRoleMenuRight.appName = row.name;
};

const query = async () => {
    if (props.id !== "0") {
        response.loading = true;
        const data = await apiGetAppList(props.id);
        response.appList = data?.records || [];
        response.loading = false;
    }
};

// 解绑应用
const unBindApp = async (appId: string) => {
    ElMessageBox.confirm("此操作将解绑该应用, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        center: true,
    })
        .then(async () => {
            const bool = await apiUnBindApp({
                appId,
                tenantId: props.id,
            });
            bool && query();
        })
        .catch(() => {});
};

// 编辑应用
const openEdit = (app: any) => {
    editApp.show = true;
    editApp.id = app.id;
    editApp.appId = app.appId;

    editApp.status = app.status;
    editApp.validStartTime = app.validStartTime;
    editApp.validEndTime = app.validEndTime;
};

onBeforeMount(() => {
    query();
});

const emit = defineEmits(["query", "close"]);
</script>

<template>
    <GlobalElDialog width="1600px" :="$attrs" @close="emit('close')">
        <div class="app-list-box">
            <div
                class="app-list-left"
                :style="{ width: configRoleMenuRight.show ? '300px' : '0px' }"
            >
                <div style="margin: 0 10px" v-if="configRoleMenuRight.show">
                    {{ configRoleMenuRight.appName }}
                </div>
                <!-- 配置角色菜单权限弹窗 -->
                <ConfigRoleMenuRight
                    v-if="configRoleMenuRight.show"
                    :id="configRoleMenuRight.appId"
                    :tenantId="props.id"
                    @query="query"
                    @close="
                        () => {
                            configRoleMenuRight.show = false;
                        }
                    "
                />
            </div>
            <div
                class="app-list-right"
                :style="{
                    width: configRoleMenuRight.show
                        ? 'calc(100% - 300px)'
                        : '100%',
                }"
            >
                <!-- 新增应用按钮 -->
                <div style="margin-bottom: 20px; text-align: right">
                    <el-button type="primary" @click="openAddApp">
                        新增应用
                    </el-button>
                </div>

                <!-- 已绑定app列表 -->
                <el-table
                    :data="response.appList"
                    border
                    v-loading="response.loading"
                    stripe
                    style="width: 100%"
                >
                    <el-table-column
                        prop="name"
                        label="应用名称"
                        width="180"
                        align="center"
                    />
                    <el-table-column
                        prop="status"
                        label="状态"
                        width="120"
                        align="center"
                    >
                        <template #default="{ row }">
                            <el-tag
                                :type="
                                    row.status === '1' ? 'success' : 'danger'
                                "
                            >
                                {{ row.status === "1" ? "正常" : "停用" }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column
                        prop="validStartTime"
                        label="有效期开始时间"
                        min-width="200"
                        align="center"
                    />
                    <el-table-column
                        prop="validEndTime"
                        label="有效期结束时间"
                        min-width="200"
                        align="center"
                    />
                    <el-table-column label="操作" width="280" align="center">
                        <template #default="{ row }">
                            <el-button
                                type="primary"
                                size="small"
                                @click="openEdit(row)"
                            >
                                编辑
                            </el-button>
                            <el-button
                                type="primary"
                                size="small"
                                @click="openConfigRoleMenuRight(row)"
                            >
                                配置菜单权限
                            </el-button>
                            <el-button
                                type="danger"
                                size="small"
                                @click="unBindApp(row.appId)"
                            >
                                解绑
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>

                <!-- 编辑应用弹窗 -->
                <OperatApp
                    :show="editApp.show"
                    :id="editApp.id"
                    :tenant-id="props.id"
                    :app-id="editApp.appId"
                    :status="editApp.status"
                    :valid-start-time="editApp.validStartTime"
                    :valid-end-time="editApp.validEndTime"
                    @update:show="editApp.show = $event"
                    @query="query"
                    @close="editApp.show = false"
                />

                <!-- 新增应用弹窗 -->
                <OperatApp
                    :show="addApp.show"
                    :tenant-id="props.id"
                    @update:show="addApp.show = $event"
                    @query="query"
                    @close="addApp.show = false"
                />
            </div>
        </div>
    </GlobalElDialog>
</template>

<style scoped>
.app-list-box {
    display: flex;
    height: 700px;
    flex-direction: row-reverse;
}

.app-list-left {
    width: 300px;
}

.app-list-right {
    flex: 1;
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "应用列表",
    name: "AppList",
});
</script>
