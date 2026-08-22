<script setup lang="ts">
import { reactive, onBeforeMount, ref, nextTick } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import {
    CirclePlus, //添加
    Delete, // 删除
} from "@element-plus/icons-vue";

import { apiGetList, apiDelete, initParams, apiStatus } from "./api";
import BtnAuth from "./auth_btn";
import ConfigRoleMenuRight from "./components/ConfigRoleMenuRight.vue";
import OperatData from "./components/OperatData.vue";
import OperatDomain from "./components/OperatDomain.vue";
import OperatApp from "./components/OperatApp.vue";
import AppList from "./components/AppList.vue";

import AddData from "./components/AddData.vue";
import LookData from "./components/LookData.vue";
import type { VxeTableEvents } from "vxe-table";
import type { orderByDTOSParams } from "@/types/index.ts";
const refVxeTable = ref();
const xToolbar = ref();
const loading = ref<boolean>(false);
nextTick(() => /* 将表格和工具栏进行关联*/ {
    const $table = refVxeTable.value;
    $table?.connect(xToolbar.value);
});

const opInit = {
    show: false,
    code: "init", // "look" "edit" "add"
    id: "0", //id为0时表示添加
    tenantName: "",
};
const opObject = reactive({
    ...opInit,
});
const close = () => /* 关闭模态框 */ {
    Object.assign(opObject, opInit);
};

const ajaxParams = reactive({ ...initParams });
const reset = () => /* 重置按钮 */ {
    Object.assign(ajaxParams, initParams);
    getList();
};
const response = reactive({
    records: [],
    total: 0,
});
async function getList() {
    refVxeTable.value?.clearCheckboxRow();
    loading.value = true;
    const { total, records } = await apiGetList(ajaxParams);
    response.records = records;
    response.total = total;
    loading.value = false;
}
onBeforeMount(() => {
    getList();
});

const changeStatus = async (val: string | Array<string>, status?: number) => {
    let bool: boolean | undefined = false;
    if (Array.isArray(val)) {
        const ids: Array<string> = refVxeTable.value
            ?.getCheckboxRecords(true)
            .map((item: { id: any }) => item.id);
        if (ids.length === 0) {
            ElMessage({
                message: "请选择数据后进行操作",
                type: "warning",
                center: true,
            });
            return;
        }
        bool = await apiStatus(ids, status);
    } else {
        bool = await apiStatus(val);
    }
    bool && getList();
};

const del = (val: string | Array<string>) => {
    let ids: Array<string>;
    if (Array.isArray(val)) {
        ids = refVxeTable.value
            ?.getCheckboxRecords(true)
            .map((item: { id: any }) => item.id);
        if (ids.length === 0) {
            ElMessage({
                message: "请选择数据后进行操作",
                type: "warning",
                center: true,
            });
            return;
        }
    }
    ElMessageBox.confirm("此操作将永久删除选择数据, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        center: true,
    })
        .then(async () => {
            const bool = await apiDelete(Array.isArray(val) ? ids : val);
            bool && (ajaxParams.current = 1);
            getList();
        })
        .catch(() => {});
};

const sortChangeEvent: VxeTableEvents.SortChange<any> = ({ sortList }) => {
    const res: Array<orderByDTOSParams> = [];
    sortList.map((item: any) => {
        res.push({
            asc: item.order == "asc" ? true : false,
            column: item.field,
        });
    });
    ajaxParams.orderByDTOS = res;
    getList();
};
</script>

<template>
    <LookData
        title="查看"
        v-if="opObject.code === 'look'"
        :id="opObject.id"
        v-model="opObject.show"
        @close="close"
    />

    <OperatData
        v-if="opObject.code === 'edit'"
        :title="opObject.code === 'add' ? '添加' : '编辑'"
        :id="opObject.id"
        v-model="opObject.show"
        @query="getList()"
        @close="close"
    />
    <OperatDomain
        v-if="opObject.code === 'editDomain'"
        title="配置域名"
        :id="opObject.id"
        v-model="opObject.show"
        @query="getList()"
        @close="close"
    />

    <AppList
        v-if="opObject.code === 'editApp'"
        title="配置应用"
        :id="opObject.id"
        v-model="opObject.show"
        @query="getList()"
        @close="close"
    />

    <AddData
        v-if="opObject.code === 'add'"
        :title="opObject.code === 'add' ? '添加' : '编辑'"
        :id="opObject.id"
        v-model="opObject.show"
        @query="getList()"
        @close="close"
    />

    <div class="role-manage-box">
        <WrapTableLayout
            class="left-box"
            @query="getList()"
            @reset="reset"
            more
        >
            <template #header>
                <WrapToolbarSearchLayout label="租户名称">
                    <GlobalSearchInput
                        @query="getList()"
                        v-model="ajaxParams.name"
                        placeholder="请输入租户名查询"
                    ></GlobalSearchInput>
                </WrapToolbarSearchLayout>
                <WrapToolbarSearchLayout label="租户编码">
                    <GlobalSearchInput
                        @query="getList()"
                        v-model="ajaxParams.code"
                        placeholder="请输入租户编码查询"
                    ></GlobalSearchInput>
                </WrapToolbarSearchLayout>
            </template>

            <template #drop> </template>

            <template #toolbar>
                <vxe-toolbar ref="xToolbar" custom>
                    <template #buttons>
                        <GlobalAuthElButton
                            :auth="BtnAuth.add"
                            :startAuth="true"
                            type="primary"
                            @click.stop="
                                opObject.show = true;
                                opObject.code = 'add';
                            "
                            >添加</GlobalAuthElButton
                        >

                        <GlobalAuthElButton
                            :auth="BtnAuth.batDel"
                            :startAuth="true"
                            type="danger"
                            @click.stop="del([])"
                            >删除</GlobalAuthElButton
                        >
                    </template>

                    <template #tools> </template>
                </vxe-toolbar>
            </template>

            <vxe-table
                ref="refVxeTable"
                align="center"
                :data="response.records"
                :row-config="{ isCurrent: true, isHover: true }"
                size="medium"
                border="inner"
                :loading="loading"
                show-overflow
                height="auto"
                :auto-resize="true"
                :sync-resize="true"
                :sort-config="{
                    multiple: true,
                    chronological: false,
                }"
                @sort-change="sortChangeEvent"
            >
                <vxe-column type="checkbox" width="45" title=""></vxe-column>
                <vxe-column type="seq" title="序号" width="50"></vxe-column>

                <vxe-column title="租户名称" field="name" sortable>
                    <template #default="{ row }">
                        {{ row.name }}
                    </template>
                </vxe-column>
                <vxe-column title="租户编码" field="code" sortable>
                    <template #default="{ row }">
                        {{ row.code }}
                    </template>
                </vxe-column>

                <!-- <vxe-column title="岗位编码">
                <template #default="{ row }">
                    {{ row.postCode }}
                </template>
            </vxe-column> -->

                <!-- <vxe-column title="租户排序" field="sort" sortable>
                <template #default="{ row }">
                    {{ row.sort }}
                </template>
            </vxe-column> -->
                <vxe-column title="创建时间" field="createTime" sortable />
                <vxe-column title="修改时间" field="modifyTime" sortable />
                <vxe-column title="启用停用" width="100">
                    <!-- 启用停用(0启用1停用) -->
                    <template #default="{ row }">
                        <GlobalElSwitch
                            @change="changeStatus(row.id)"
                            v-model="row.status"
                            active-value="0"
                            inactive-value="1"
                        />
                    </template>
                </vxe-column>
                <!-- <vxe-column field="remarks" title="备注" sortable> </vxe-column> -->
                <vxe-column field="" title="操作" width="360">
                    <template #default="{ row }">
                        <GlobalAuthElButtonLink
                            :auth="BtnAuth.editDomain"
                            size="small"
                            type="warning"
                            link
                            @click="
                                opObject.id = row.id;
                                opObject.show = true;
                                opObject.code = 'editDomain';
                                opObject.tenantName = row.name;
                            "
                        >
                            <!-- <el-icon>
                            <Edit />
                        </el-icon> -->
                            配置域名
                        </GlobalAuthElButtonLink>
                        <GlobalAuthElButtonLink
                            :auth="BtnAuth.editDomain"
                            size="small"
                            type="warning"
                            link
                            @click="
                                opObject.id = row.id;
                                opObject.show = true;
                                opObject.code = 'editApp';
                                opObject.tenantName = row.name;
                            "
                        >
                            <!-- <el-icon>
                            <Edit />
                        </el-icon> -->
                            配置应用
                        </GlobalAuthElButtonLink>
                        <!-- <GlobalAuthElButtonLink
                            :auth="BtnAuth.editMenu"
                            :startAuth="true"
                            size="small"
                            type="warning"
                            link
                            @click="
                                opObject.id = row.id;
                                opObject.show = true;
                                opObject.code = 'menu';
                                opObject.tenantName = row.name;
                            "
                        >
                            配置菜单权限
                        </GlobalAuthElButtonLink> -->
                        <GlobalAuthElButtonLink
                            :auth="BtnAuth.edit"
                            :startAuth="true"
                            size="small"
                            type="primary"
                            link
                            @click="
                                opObject.id = row.id;
                                opObject.show = true;
                                opObject.code = 'edit';
                            "
                        >
                            <!-- <el-icon>
                            <Edit />
                        </el-icon> -->
                            编辑
                        </GlobalAuthElButtonLink>

                        <!-- <GlobalAuthElButton
                        :auth="BtnAuth.look"
                        size="small"
                        type="info"
                        @click="
                            opObject.id = row.id;
                            opObject.show = true;
                            opObject.code = 'look';
                        "
                    >
                        <el-icon>
                            <ZoomIn />
                        </el-icon>
                    </GlobalAuthElButton> -->

                        <GlobalAuthElButtonLink
                            :auth="BtnAuth.del"
                            :startAuth="true"
                            size="small"
                            type="danger"
                            link
                            @click="del(row.id)"
                        >
                            <!-- <el-icon>
                            <Delete />
                        </el-icon> -->
                            删除
                        </GlobalAuthElButtonLink>
                    </template>
                </vxe-column>
            </vxe-table>

            <template #pagination>
                <GlobalElPagination
                    v-model:currentPage="ajaxParams.current"
                    v-model:page-size="ajaxParams.size"
                    :total="response.total"
                    @size-change="getList"
                    @current-change="getList"
                />
            </template>
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
                租户 : &nbsp;{{ opObject.tenantName }}
                <el-icon
                    :size="20"
                    style="cursor: pointer"
                    @click="opObject.show = false"
                    ><CircleClose color="#D9D9D9"
                /></el-icon>
            </div>
            <div class="right-content-box">
                <ConfigRoleMenuRight
                    v-if="opObject.code === 'menu' && opObject.show"
                    :id="opObject.id"
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
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "岗位管理",
    name: "PostManage",
});
</script>
