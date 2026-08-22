<script setup lang="ts">
import { reactive, onBeforeMount, ref, nextTick, computed } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import $api from "@/api/Axios";
import {
    CirclePlus, //添加
    Delete, // 删除
} from "@element-plus/icons-vue";

import { apiGetList, apiDelete, initParams, importOrg } from "./api";
import BtnAuth from "./auth_btn";

import ConfigPostRole from "./components/ConfigPostRole.vue";
import OperatData from "./components/OperatData.vue";
import LookData from "./components/LookData.vue";
import BindUser from "./components/BindUser.vue";
import type { VxeTableEvents } from "vxe-table";
const refVxeTable = ref();
const xToolbar = ref();
nextTick(() => {
    // 将表格和工具栏进行关联
    const $table = refVxeTable.value;
    $table?.connect(xToolbar.value);
});

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

const opInit = {
    show: false,
    code: "init", // "look" "edit" "add" "role"
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
    records: [],
});

async function getList() {
    refVxeTable.value?.clearCheckboxRow();
    loading.value = true;
    const data = await apiGetList(ajaxParams);
    response.records = data.records;
    TreeExpand(flag.value);
    loading.value = false;
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
    }).then(async () => {
        const bool = await apiDelete(Array.isArray(val) ? ids : val);
        bool && getList();
    });
};

const btnColor = computed(() => {
    return "red";
});

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

const isSync = ref(false);
const importSyncOrg = async () => {
    isSync.value = true;
    const bool = await importOrg();
    isSync.value = false;
    bool && getList();
};
</script>

<template>
    <ConfigPostRole
        title="配置组织角色"
        v-if="opObject.code === 'role'"
        :id="opObject.id"
        v-model="opObject.show"
        @query="getList()"
        @close="close"
    />

    <LookData
        title="查看"
        v-if="opObject.code === 'look'"
        :id="opObject.id"
        v-model="opObject.show"
        @close="close"
    />
    <BindUser
        title="同步组织"
        v-if="opObject.code === 'bind'"
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
            <WrapToolbarSearchLayout label="组织名称">
                <GlobalSearchInput
                    @query="getList()"
                    v-model="ajaxParams.departName"
                    placeholder="请输入组织名查询"
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

                    <GlobalAuthElButton
                        :auth="BtnAuth.batDel"
                        type="danger"
                        @click.stop="del([])"
                        >删除</GlobalAuthElButton
                    >
                    <GlobalAuthElButton
                        :auth="BtnAuth.importData"
                        type="primary"
                        :loading="isSync"
                        @click.stop="
                            opObject.show = true;
                            opObject.code = 'bind';
                        "
                        >同步广政通组织</GlobalAuthElButton
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
            :row-config="{ isCurrent: true, isHover: true }"
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
                line: true,
            }"
            :sort-config="{
                multiple: true,
                chronological: false,
            }"
            @sort-change="sortChangeEvent"
        >
            <vxe-column type="checkbox" width="45" />
            <vxe-column
                title="组织名称"
                tree-node
                width="145"
                field="departName"
                sortable
            />

            <vxe-column title="组织编码" field="code">
                <template #default="{ row }">
                    {{ row.code || "--" }}
                </template>
            </vxe-column>

            <vxe-column title="组织负责人" field="userName">
                <template #default="{ row }">
                    {{ (row.sysUserVO && row.sysUserVO.realName) || "--" }}
                </template>
            </vxe-column>

            <vxe-column title="排序" field="sort" sortable />
            <vxe-column title="创建时间" field="createTime" sortable />
            <vxe-column title="修改时间" field="modifyTime" sortable />

            

            <vxe-column field="" title="操作" width="300">
                <template #default="{ row }">
                    <!-- <GlobalAuthElButton
                        :auth="BtnAuth.role"
                        size="small"
                        type="warning"
                        @click="
                            opObject.id = row.id;
                            opObject.show = true;
                            opObject.code = 'role';
                        "
                    >
                        <el-icon>
                            <User />
                        </el-icon>
                    </GlobalAuthElButton> -->
                   
                    <GlobalAuthElButtonLink
                        :auth="BtnAuth.edit"
                        size="small"
                        link
                        type="primary"
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
                        link
                        type="primary"
                        @click="
                            opObject.id = row.id;
                            opObject.show = true;
                            opObject.code = 'look';
                        "
                    >
                        查看
                    </GlobalAuthElButton> -->

                    <GlobalAuthElButtonLink
                        :auth="BtnAuth.del"
                        size="small"
                        link
                        type="danger"
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
                        link
                        type="primary"
                        @click="
                            opObject.parentId = row.id;
                            opObject.show = true;
                            opObject.code = 'add';
                        "
                    >
                        <!-- <el-icon>
                            <ZoomIn />
                        </el-icon> -->
                        添加下级组织
                    </GlobalAuthElButtonLink>
                </template>
            </vxe-column>
        </vxe-table>
    </WrapTableLayout>
</template>

<style scoped></style>

<script lang="ts">
import { defineComponent } from "vue";
import type { orderByDTOSParams } from "@/types";
export default defineComponent({
    title: "组织管理",
    name: "DepartManage",
});
</script>
