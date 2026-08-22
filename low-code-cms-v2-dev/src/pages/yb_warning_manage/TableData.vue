<script setup lang="ts">
import { reactive, onBeforeMount, ref, nextTick } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import {
    CirclePlus, //添加
    Delete, // 删除
} from "@element-plus/icons-vue";

import { apiGetList, apiDelete, initParams } from "./api";
import BtnAuth from "./auth_btn";

import OperatData from "./components/OperatData.vue";
import LookData from "./components/LookData.vue";

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
    />

    <WrapTableLayout style="margin: 0" @query="getList()" @reset="reset" more>
        <template #header>
            <WrapToolbarSearchLayout label="岗位名称">
                <GlobalSearchInput
                    @query="getList()"
                    v-model="ajaxParams.postName"
                    placeholder="请输入岗位名查询"
                ></GlobalSearchInput>
            </WrapToolbarSearchLayout>
        </template>

        <template #drop> </template>

        <!-- <template #toolbar>
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

                <template #tools> </template>
            </vxe-toolbar>
        </template> -->

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
            style="margin-top: 10px"
        >
            <vxe-column type="checkbox" width="45" title=""></vxe-column>
            <vxe-column type="seq" title="序号" width="50"></vxe-column>

            <vxe-column title="岗位名称">
                <template #default="{ row }">
                    {{ row.postName }}
                </template>
            </vxe-column>

            <!-- <vxe-column title="岗位编码">
                <template #default="{ row }">
                    {{ row.postCode }}
                </template>
            </vxe-column> -->

            <vxe-column title="岗位排序">
                <template #default="{ row }">
                    {{ row.sort }}
                </template>
            </vxe-column>
            <vxe-column title="创建时间" field="createTime" />

            <vxe-column field="remarks" title="备注"> </vxe-column>
            <vxe-column field="" title="操作" width="180">
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
</template>

<style scoped></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "岗位管理",
    name: "PostManage",
});
</script>
