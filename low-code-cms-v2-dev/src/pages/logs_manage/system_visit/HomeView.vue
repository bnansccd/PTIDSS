<script setup lang="ts">
import { reactive, onBeforeMount, ref, nextTick } from "vue";

import { apiGetList, initParams } from "./api";
import BtnAuth from "./auth_btn";

import LookData from "./components/LookData.vue";
import type { VxeTableEvents } from "vxe-table";
const refVxeTable = ref();
const xToolbar = ref();
nextTick(() => {
    // 将表格和工具栏进行关联
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
const dataDict = useDataDict();
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
    const p1 = dataDict.getDictList("OPERATE_STATUS");
    const { total, records } = await apiGetList(ajaxParams);

    p1.then(() => {
        response.records = records.map((item) => {
            const statusName = dataDict.getDictListValbyId(
                "OPERATE_STATUS",
                item.status
            );
            return Object.assign({ statusName: statusName }, item);
        });
    });
    response.records = records;
    response.total = total;
}
onBeforeMount(() => {
    getList();
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
</script>

<template>
    <LookData
        title="查看"
        v-if="opObject.code === 'look'"
        :id="opObject.id"
        v-model="opObject.show"
        @close="close"
    />

    <WrapTableLayout @query="getList()" @reset="reset" more>
        <template #header>
            <WrapToolbarSearchLayout
                label="用户账号"
                style="margin-right: 0.6vw"
            >
                <GlobalSearchInput
                    @query="getList()"
                    v-model="ajaxParams.userName"
                    placeholder="请输入用户账号查询"
                ></GlobalSearchInput>
            </WrapToolbarSearchLayout>

            <WrapToolbarSearchLayout label="日期筛选">
                <GlobalDatePicker
                    v-model:startTime="ajaxParams.startTime"
                    v-model:endTime="ajaxParams.endTime"
                    value-format="YYYY-MM-DD HH:mm:ss"
                />
            </WrapToolbarSearchLayout>
        </template>

        <template #drop> </template>

        <template #toolbar>
            <vxe-toolbar ref="xToolbar" custom>
                <template #buttons> </template>
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
            :sort-config="{
                multiple: true,
                chronological: false,
            }"
            @sort-change="sortChangeEvent"
        >
            <vxe-column type="seq" title="序号" width="50"></vxe-column>

            <vxe-column title="用户账号" field="username" sortable>
                <template #default="{ row }">
                    {{ row.username }}
                </template>
            </vxe-column>

            <vxe-column title="访问时间" field="accessTime" sortable>
                <template #default="{ row }">
                    {{ row.accessTime }}
                </template>
            </vxe-column>

            <vxe-column title="登录IP地址" field="loginIp" sortable>
                <template #default="{ row }">
                    {{ row.loginIp }}
                </template>
            </vxe-column>

            <vxe-column title="提示信息" field="msg" sortable>
                <template #default="{ row }">
                    {{ row.msg }}
                </template>
            </vxe-column>

            <vxe-column title="登录状态" field="status" sortable>
                <template #default="{ row }">
                    {{ row.statusName }}
                </template>
            </vxe-column>

            <!-- <vxe-column field="" title="操作" width="100">
                <template #default="{ row }">
                    <GlobalAuthElButton
                        :auth="BtnAuth.look"
                        size="small"
                        type="info"
                        link
                        @click="
                            opObject.show = true;
                            opObject.code = 'look';
                            opObject.id = row.id;
                        "
                    >
                        查看
                    </GlobalAuthElButton>
                </template>
            </vxe-column> -->
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
import type { orderByDTOSParams } from "@/types";
import { useDataDict } from "@/stores/modules/dataDict";
export default defineComponent({
    title: "系统访问",
    name: "SystemVisit",
});
</script>
