<script setup lang="ts">
import { reactive, onBeforeMount, ref, nextTick } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import {
    CirclePlus, //添加
    Delete, // 删除
} from "@element-plus/icons-vue";

import { apiGetList, apiDelete, initParams, apiStatus } from "./api";
import BtnAuth from "./auth_btn";

import OperatData from "./components/OperatData.vue";
import LookData from "./components/LookData.vue";
import { baseStaticUrl } from "@/env/index";
const refVxeTable = ref();
const xToolbar = ref();
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
    const { total, records } = await apiGetList(ajaxParams);
    response.records = records;
    response.total = total;
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
        bool = await apiStatus(val, status);
    }
    bool && getList();
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

    <WrapTableLayout @query="getList()" @reset="reset" :showHeader="false" more>
        <template #header>
            <!-- <GlobalSearchInput
                @query="getList()"
                v-model="ajaxParams.postName"
                placeholder="请输入岗位名查询"
            ></GlobalSearchInput> -->
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
            show-overflow
            height="auto"
            :auto-resize="true"
            :sync-resize="true"
        >
            <vxe-column type="checkbox" width="45" title=""></vxe-column>
            <vxe-column type="seq" title="序号" width="50"></vxe-column>

            <vxe-column title="标题" field="title">
                <template #default="{ row }">
                    {{ row.title }}
                </template>
            </vxe-column>

            <vxe-column title="描述" field="summary">
                <template #default="{ row }">
                    {{ row.summary }}
                </template>
            </vxe-column>

            <vxe-column title="封面" field="photoUrl">
                <template #default="{ row }">
                    <el-image
                        style="width: 50px; height: 20px"
                        :src="baseStaticUrl + row.photoUrl"
                        :zoom-rate="1.2"
                        :initial-index="4"
                        fit="cover"
                    />
                </template>
            </vxe-column>

            <vxe-column field="publishDate" title="时间"> </vxe-column>
            <!-- <vxe-column field="text" title="内容"> </vxe-column> -->

            <vxe-column title="状态" field="status">
                <template #default="{ row }">
                    <GlobalElSwitch
                        @change="changeStatus(row.id, row.status)"
                        v-model="row.status"
                        active-value="1"
                        inactive-value="0"
                    />
                </template>
            </vxe-column>
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
                        编辑
                    </GlobalAuthElButtonLink>

                    <GlobalAuthElButtonLink
                        :auth="BtnAuth.look"
                        size="small"
                        type="primary"
                        link
                        @click="
                            opObject.id = row.id;
                            opObject.show = true;
                            opObject.code = 'look';
                        "
                    >
                        查看
                    </GlobalAuthElButtonLink>

                    <GlobalAuthElButtonLink
                        :auth="BtnAuth.del"
                        size="small"
                        type="danger"
                        link
                        @click="del(row.id)"
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
</template>

<style scoped></style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "岗位管理",
    name: "PostManage",
});
</script>
