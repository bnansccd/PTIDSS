<script setup lang="ts">
import { reactive, onBeforeMount, ref, nextTick, onUnmounted } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import {
    CirclePlus, //添加
    Delete, // 删除
    Open, // 启用
    TurnOff, // 停用
} from "@element-plus/icons-vue";

import type { VxeTableInstance } from "vxe-table";
import Sortable from "sortablejs";
import XEUtils from "xe-utils";

import {
    apiGetList,
    apiDelete,
    apiStatus,
    apiShow,
    apiDrag,
    initParams,
} from "./api";
import BtnAuth from "./auth_btn";

import OperatData from "./components/OperatData.vue";
import LookData from "./components/LookData.vue";
import type { VxeTableEvents } from "vxe-table";
const flag = ref<boolean>(false); //true 表示展开

const loading = ref<boolean>(false);
const TreeExpand = (bool: boolean) => {
    flag.value = bool;
    nextTick(() => {
        if (bool) {
            refVxeTable.value?.setAllTreeExpand(true); // 展开所有
        } else {
            refVxeTable.value?.clearTreeExpand(); // 关闭所有
        }
    });
};

const refVxeTable = ref<VxeTableInstance>();
const xToolbar = ref();
const dataDict = useDataDict();
const opInit = {
    show: false,
    code: "init", // "look" "edit" "add"
    id: "0", //id为0时表示添加
    parentId: "",
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
    showHelpTip: false,
    records: [],
});

let sortable: any;
const treeDrop = () => {
    const $grid = refVxeTable.value;
    sortable = Sortable.create(
        $grid?.$el.querySelector(
            ".body--wrapper>.vxe-table--body tbody"
        ) as HTMLElement,
        {
            handle: ".drag-btn",
            onEnd: async (sortableEvent: any) => {
                const targetTrElem = sortableEvent.item;
                const oldIndex = sortableEvent.oldIndex as number;
                const options = { children: "children" };
                const wrapperElem = targetTrElem.parentNode as HTMLElement;
                const prevTrElem =
                    targetTrElem.previousElementSibling as HTMLElement;
                const tableTreeData = response.records as any[];
                const targetRowNode = $grid?.getRowNode(targetTrElem);
                if (!targetRowNode) {
                    return;
                }
                const selfRow = targetRowNode.item;
                const selfNode = XEUtils.findTree(
                    tableTreeData,
                    (row) => row === selfRow,
                    options
                );
                if (prevTrElem) {
                    // 移动到节点
                    const prevRowNode = $grid?.getRowNode(prevTrElem);
                    if (!prevRowNode) {
                        return;
                    }
                    const prevRow = prevRowNode.item;
                    const prevNode = XEUtils.findTree(
                        tableTreeData,
                        (row) => row === prevRow,
                        options
                    );
                    if (
                        XEUtils.findTree(
                            selfRow[options.children],
                            (row) => prevRow === row,
                            options
                        )
                    ) {
                        // 错误的移动
                        const oldTrElem = wrapperElem.children[oldIndex];
                        wrapperElem.insertBefore(targetTrElem, oldTrElem);
                        return null;
                    }
                    const currRow = selfNode.items.splice(selfNode.index, 1)[0];
                    console.log(currRow.menuName);
                    console.log(prevRow.menuName);
                    if (await apiDrag(currRow.id, prevRow.id)) {
                        getList();
                    }
                } else {
                    // 移动到第一行
                    const currRow = selfNode.items.splice(selfNode.index, 1)[0];

                    if (await apiDrag(currRow.id)) {
                        getList();
                    }
                }
            },
        }
    );
};

let initTime: any;
nextTick(() => {
    // 将表格和工具栏进行关联
    const $table = refVxeTable.value;
    $table?.connect(xToolbar.value);
    initTime = setTimeout(() => {
        treeDrop();
    }, 500);
});

onUnmounted(() => {
    clearTimeout(initTime);
    if (sortable) {
        sortable.destroy();
    }
});

async function getList() {
    refVxeTable.value?.clearCheckboxRow();
    loading.value = true;
    const p1 = dataDict.getDictList("MENU_TYPE");
    const p2 = dataDict.getDictList("TRUE_FALSE");
    const data = await apiGetList(ajaxParams);

    Promise.all([p1, p2]).then(() => {
        response.records = data.records.map((item) => {
            const menuTypeName = dataDict.getDictListValbyId(
                "MENU_TYPE",
                item.menuType
            );
            const isBaseName = dataDict.getDictListValbyId(
                "TRUE_FALSE",
                item.isBase
            );
            return Object.assign(
                { menuTypeName: menuTypeName, isBaseName: isBaseName },
                item
            );
        });
        TreeExpand(flag.value);
        loading.value = false;
    });
}
onBeforeMount(() => {
    getList();
});

const del = (val: string | Array<string>) => {
    const ids: Array<string> = [];
    if (Array.isArray(val)) {
        refVxeTable.value?.getCheckboxRecords(true).forEach((item: any) => {
            // 处理树形批量删除
            if (item.children.length === 0) {
                ids.push(item.id);
            }
        });
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
            bool && getList();
        })
        .catch(() => {});
};

const changeStatus = async (val: string | Array<string>, status?: number) => {
    let bool: boolean | undefined = false;
    if (Array.isArray(val)) {
        const ids: Array<number | string> | any = refVxeTable.value
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

const changeShow = async (val: string | Array<string>, status?: number) => {
    let bool: boolean | undefined = false;
    if (Array.isArray(val)) {
        const ids: Array<number | string> | any = refVxeTable.value
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
        bool = await apiShow(ids, status);
    } else {
        bool = await apiShow(val);
    }
    bool && getList();
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
        v-if="opObject.code === 'add' || opObject.code === 'edit'"
        :title="opObject.code === 'add' ? '添加' : '编辑'"
        :id="opObject.id"
        v-model="opObject.show"
        @query="getList()"
        @close="close"
        :parent-id="opObject.parentId"
    />

    <WrapTableLayout @query="getList()" @reset="reset" more>
        <template #header>
            <WrapToolbarSearchLayout label="菜单名称">
                <GlobalSearchInput
                    @query="getList()"
                    v-model="ajaxParams.menuName"
                    placeholder="请输入菜单名查询"
                ></GlobalSearchInput>
            </WrapToolbarSearchLayout>
        </template>

        <template #drop> </template>

        <template #toolbar>
            <vxe-toolbar ref="xToolbar" custom>
                <template #buttons>
                    <GlobalAuthElButton
                        :auth="BtnAuth.add"
                        type="primary"
                        @click.stop="
                            opObject.show = true;
                            opObject.code = 'add';
                        "
                        >添加</GlobalAuthElButton
                    >
                    <!-- <GlobalAuthElButton
                        :auth="BtnAuth.importData"
                        type="success"
                        :icon="Open"
                        @click="changeStatus([], 0)"
                        >启用</GlobalAuthElButton
                    >
                    <GlobalAuthElButton
                        :auth="BtnAuth.exportData"
                        type="warning"
                        :icon="TurnOff"
                        @click="changeStatus([], 1)"
                        >停用</GlobalAuthElButton
                    > -->
                    <GlobalAuthElButton
                        :auth="BtnAuth.batDel"
                        type="danger"
                        @click.stop="del([])"
                        >删除</GlobalAuthElButton
                    >
                </template>

                <template #tools>
                    <GlobalUnfoldFewer :flag="flag" @TreeExpand="TreeExpand" />
                </template>
            </vxe-toolbar>
        </template>

        <vxe-table
            ref="refVxeTable"
            align="center"
            :data="response.records"
            :row-config="{ isCurrent: true, isHover: true, useKey: true }"
            size="medium"
            border="inner"
            :loading="loading"
            show-overflow
            height="auto"
            :auto-resize="true"
            :sync-resize="true"
            :column-config="{ resizable: true }"
            :tree-config="{
                transform: true,
                rowField: 'id',
                parentField: 'parentId',
            }"
            :sort-config="{
                multiple: true,
                chronological: false,
            }"
            @sort-change="sortChangeEvent"
        >
            <vxe-column type="checkbox" width="45"></vxe-column>
            <!--
            <vxe-column width="60">
                <template #default>
                    <span class="drag-btn">
                        <el-icon :size="15">
                            <Rank />
                        </el-icon>
                    </span>
                </template>
                <template #header>
                    <vxe-tooltip
                        v-model="response.showHelpTip"
                        content="按住后可以上下拖动排序！"
                        enterable
                    >
                        <i
                            class="vxe-icon-question-circle-fill"
                            @click="
                                response.showHelpTip = !response.showHelpTip
                            "
                        ></i>
                    </vxe-tooltip>
                </template>
            </vxe-column> -->

            <vxe-column tree-node title="菜单名称" field="menuName" sortable>
                <template #default="{ row }">
                    {{ row.menuName }}
                </template>
            </vxe-column>

            <vxe-column title="菜单类型" field="menuType" sortable>
                <template #default="{ row }">
                    {{ row.menuTypeName }}
                </template>
            </vxe-column>

            <vxe-column title="基础菜单" field="isBase" sortable>
                <template #default="{ row }">
                    {{ row.isBaseName }}
                </template>
            </vxe-column>

            <vxe-column title="路由地址" field="href" sortable>
                <template #default="{ row }">
                    {{ row.href }}
                </template>
            </vxe-column>

            <vxe-column title="权限code" field="menuCode" sortable>
                <template #default="{ row }">
                    {{ row.menuCode }}
                </template>
            </vxe-column>

            <!-- <vxe-column title="图标">
                <template #default="{ row }">
                    {{ row.icon }}
                </template>
            </vxe-column> -->

            <!-- <vxe-column title="是否显示">
                <template #default="{ row }">
                    {{ row.isShow }}
                </template>
            </vxe-column> -->

            <vxe-column title="排序" field="sort" sortable>
                <template #default="{ row }">
                    {{ row.sort }}
                </template>
            </vxe-column>

            <!-- <vxe-column title="状态" field="status" width="50">
                <template #default="{ row }">
                    {{ row.status === "0" ? "启用" : "停用" }}
                </template>
            </vxe-column> -->

            <vxe-column title="启用停用" width="100">
                <template #default="{ row }">
                    <GlobalElSwitch
                        @change="changeStatus(row.id)"
                        v-model="row.status"
                        active-value="0"
                        inactive-value="1"
                    />
                </template>
            </vxe-column>
            <vxe-column title="是否展示" width="100">
                <template #default="{ row }">
                    <GlobalElSwitch
                        @change="changeShow(row.id)"
                        v-model="row.isShow"
                        active-value="0"
                        inactive-value="1"
                    />
                </template>
            </vxe-column>

            <vxe-column field="" title="操作" width="200">
                <template #default="{ row }">
                    <GlobalAuthElButtonLink
                        :auth="BtnAuth.edit"
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
                    <!--
                    <GlobalAuthElButton
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
                    <GlobalAuthElButtonLink
                        :auth="BtnAuth.addChildren"
                        size="small"
                        type="primary"
                        link
                        @click="
                            opObject.parentId = row.id;
                            opObject.show = true;
                            opObject.code = 'add';
                        "
                    >
                        <!-- <el-icon>
                            <Edit />
                        </el-icon> -->
                        添加下级菜单
                    </GlobalAuthElButtonLink>
                </template>
            </vxe-column>
        </vxe-table>

        <!-- <template #pagination>
            <GlobalElPagination
                v-model:currentPage="ajaxParams.current"
                v-model:page-size="ajaxParams.size"
                :total="response.total"
                @size-change="getList"
                @current-change="getList"
            />
        </template> -->
    </WrapTableLayout>
</template>

<style scoped></style>

<script lang="ts">
import { defineComponent } from "vue";
import { useDataDict } from "@/stores/modules/dataDict";
export default defineComponent({
    title: "菜单管理",
    name: "MenuManage",
});
</script>
