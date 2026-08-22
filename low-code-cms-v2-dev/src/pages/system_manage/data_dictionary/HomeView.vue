<script setup lang="ts">
import { reactive, onBeforeMount, ref, nextTick } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import {
    CirclePlus, //添加
    Delete, // 删除
} from "@element-plus/icons-vue";

import { apiGetList, apiDelete, initParams, childrenInitParams } from "./api";
import BtnAuth from "./auth_btn";

import OperatData from "./components/OperatData.vue";
import LookData from "./components/LookData.vue";
import type { VxeTableEvents } from "vxe-table";
const refVxeTable = ref();

const refVxeTableChildren = ref();
const xToolbar = ref();
const childrenXToolbar = ref();

const loading = ref<boolean>(false);
const loading2 = ref<boolean>(false);
nextTick(() => /* 将表格和工具栏进行关联*/ {
    const $table = refVxeTable.value;
    $table?.connect(xToolbar.value);

    const $tableChildren = refVxeTableChildren.value;
    $tableChildren?.connect(childrenXToolbar.value);
});

const flag = ref<boolean>(false); //true 表示展开
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
const chlildrenAjaxParams = reactive({ ...childrenInitParams });
const reset = () => /* 重置按钮 */ {
    Object.assign(ajaxParams, initParams);
    getList();
};
const resetChilden = () => /* 重置按钮 */ {
    //  Object.assign(chlildrenAjaxParams, childrenInitParams);
    chlildrenAjaxParams.dictName = "";
    getChildrenList();
};
const response = reactive({
    records: [],
    childrenRecords: [],
    total: 0,
    childrenTotal: 0,
});
async function getList() {
    refVxeTable.value?.clearCheckboxRow();
    loading.value = true;
    const { total, records } = await apiGetList(ajaxParams);
    response.records = records;
    response.total = total;
    TreeExpand(flag.value);
    loading.value = false;
}
async function getChildrenList() {
    if (chlildrenAjaxParams.parentId == "") {
        ElMessage({
            message: "请先选择父级字典",
            type: "warning",
            center: true,
        });
        return;
    }
    refVxeTableChildren.value?.clearCheckboxRow();
    loading2.value = true;
    const { total, records } = await apiGetList(chlildrenAjaxParams);
    response.childrenRecords = records;
    response.childrenTotal = total;
    TreeExpand(flag.value);
    loading2.value = false;
}
onBeforeMount(() => {
    getList();
});

// 表格点击事件
const onCellClick = (row: any) => {
    console.log(row.row.id);
    chlildrenAjaxParams.parentId = row.row.id;
    getChildrenList();
};

// 添加字字典
const addChild = () => {
    console.log(chlildrenAjaxParams);
    if (chlildrenAjaxParams.parentId) {
        opObject.parentId = chlildrenAjaxParams.parentId || "";
        opObject.show = true;
        opObject.code = "add";
    } else {
        ElMessage({
            message: "请先选择父级字典",
            type: "warning",
            center: true,
        });
    }
};

const del = (val: string | Array<string>) => {
    const ids: Array<string> = [];
    if (Array.isArray(val)) {
        refVxeTable.value?.getCheckboxRecords(true).forEach((item: any) => {
            // 处理树形批量删除
            if (
                !item.children ||
                (Array.isArray(item.children) && item.children.length === 0)
            ) {
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
    }).then(async () => {
        const bool = await apiDelete(Array.isArray(val) ? ids : val);
        bool && getList();
    });
};

const delChildren = (val: string | Array<string>) => {
    const ids: Array<string> = [];
    if (Array.isArray(val)) {
        refVxeTableChildren.value
            ?.getCheckboxRecords(true)
            .forEach((item: any) => {
                // 处理树形批量删除
                if (
                    !item.children ||
                    (Array.isArray(item.children) && item.children.length === 0)
                ) {
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
    }).then(async () => {
        const bool = await apiDelete(Array.isArray(val) ? ids : val);
        bool && getChildrenList();
    });
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

const sortChangeEvent2: VxeTableEvents.SortChange<any> = ({ sortList }) => {
    const res: Array<orderByDTOSParams> = [];
    sortList.map((item: any) => {
        res.push({
            asc: item.order == "asc" ? true : false,
            column: item.field,
        });
    });
    chlildrenAjaxParams.orderByDTOS = res;
    getChildrenList();
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
        :parent-id="opObject.parentId"
        @query="
            () => {
                getChildrenList();
                getList();
            }
        "
        @close="close"
    />
    <div class="dict-main-box">
        <WrapTableLayout
            class="left-box"
            @query="getList()"
            @reset="reset"
            more
        >
            <template #header>
                <WrapToolbarSearchLayout label="字典名称">
                    <GlobalSearchInput
                        @query="getList()"
                        v-model="ajaxParams.dictName"
                        placeholder="请输入字典名称查询"
                    />
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

                        <GlobalAuthElButton
                            :auth="BtnAuth.batDel"
                            type="danger"
                            @click.stop="del([])"
                            >删除</GlobalAuthElButton
                        >
                    </template>

                    <template #tools>
                        <!-- <GlobalUnfoldFewer
                            :flag="flag"
                            @TreeExpand="TreeExpand"
                        /> -->
                    </template>
                </vxe-toolbar>
            </template>

            <vxe-table
                ref="refVxeTable"
                align="center"
                :data="response.records"
                :row-config="{ isCurrent: true, isHover: true }"
                size="medium"
                border="inner"
                show-overflow
                height="auto"
                :loading="loading"
                :auto-resize="true"
                :sync-resize="true"
                :column-config="{ resizable: true }"
                :tree-config="{
                    transform: true,
                    rowField: 'id',
                    parentField: 'parentId',
                }"
                @cell-click="onCellClick"
                :sort-config="{
                    multiple: true,
                    chronological: false,
                }"
                @sort-change="sortChangeEvent"
            >
                <vxe-column type="checkbox" width="45" />
                <vxe-column
                    title="字典名称"
                    tree-node
                    field="dictName"
                    sortable
                />
                <vxe-column title="字典类型" field="dictType" sortable />
                <!-- <vxe-column title="字典父类型" field="parentType" sortable /> -->

                <vxe-column title="排序" field="sort" sortable />
                <vxe-column title="备注" field="remarks" sortable />
                <vxe-column field="" title="操作" width="160">
                    <template #default="{ row }">
                        <GlobalAuthElButtonLink
                            :auth="BtnAuth.edit"
                            size="small"
                            type="primary"
                            link
                            @click.stop="
                                opObject.id = row.id;
                                opObject.show = true;
                                opObject.code = 'edit';
                            "
                        >
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
                            size="small"
                            type="primary"
                            link
                            @click.stop="del(row.id)"
                        >
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
        <WrapTableLayout
            class="right-box"
            @query="getChildrenList()"
            @reset="resetChilden"
            more
        >
            <template #header>
                <WrapToolbarSearchLayout label="字典名称">
                    <GlobalSearchInput
                        @query="getList()"
                        v-model="chlildrenAjaxParams.dictName"
                        placeholder="请输入字典名称查询"
                    />
                </WrapToolbarSearchLayout>
            </template>

            <template #drop> </template>

            <template #toolbar>
                <vxe-toolbar ref="childrenXToolbar" custom>
                    <template #buttons>
                        <GlobalAuthElButton
                            :auth="BtnAuth.addChildren"
                            type="primary"
                            @click="addChild"
                            >添加</GlobalAuthElButton
                        >

                        <GlobalAuthElButton
                            :auth="BtnAuth.batDel"
                            type="danger"
                            @click.stop="delChildren([])"
                            >删除</GlobalAuthElButton
                        >
                    </template>

                    <template #tools>
                        <!-- <GlobalUnfoldFewer
                            :flag="flag"
                            @TreeExpand="TreeExpand"
                        /> -->
                    </template>
                </vxe-toolbar>
            </template>

            <vxe-table
                ref="refVxeTableChildren"
                align="center"
                :data="response.childrenRecords"
                :row-config="{ isCurrent: true, isHover: true }"
                size="medium"
                border="inner"
                show-overflow
                height="auto"
                :loading="loading2"
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
                @sort-change="sortChangeEvent2"
            >
                <vxe-column type="checkbox" width="45" />
                <vxe-column
                    title="字典明细"
                    width="150"
                    tree-node
                    field="dictName"
                    sortable
                />
                <vxe-column
                    title="字典明细值"
                    width="160"
                    field="dictType"
                    sortable
                />
                <!-- <vxe-column title="字典父类型" field="parentType" /> -->

                <vxe-column title="排序" field="sort" sortable />
                <vxe-column title="备注" field="remarks" sortable />

                <vxe-column field="" title="操作" width="160">
                    <template #default="{ row }">
                        <GlobalAuthElButtonLink
                            :auth="BtnAuth.edit"
                            size="small"
                            type="primary"
                            link
                            @click.stop="
                                opObject.id = row.id;
                                opObject.show = true;
                                opObject.code = 'edit';
                            "
                        >
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
                            size="small"
                            type="danger"
                            link
                            @click.stop="delChildren(row.id)"
                        >
                            删除
                        </GlobalAuthElButtonLink>
                    </template>
                </vxe-column>
            </vxe-table>
            <template #pagination>
                <GlobalElPagination
                    v-model:currentPage="chlildrenAjaxParams.current"
                    v-model:page-size="chlildrenAjaxParams.size"
                    :total="response.childrenTotal"
                    @size-change="getChildrenList"
                    @current-change="getChildrenList"
                />
            </template>
        </WrapTableLayout>
    </div>
</template>

<style lang="scss" scoped>
.dict-main-box {
    width: 100%;
    height: 100%;
    display: flex;

    .left-box {
        width: 60%;
    }

    .right-box {
        width: 40%;
        margin-left: 0 !important;
    }
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
import type { orderByDTOSParams } from "@/types";
export default defineComponent({
    title: "数据字典",
    name: "DataDictionary",
});
</script>
