<script setup lang="ts">
import { reactive, onBeforeMount, ref, nextTick } from "vue";

import { apiGetList, initParams } from "./api";
import BtnAuth from "./auth_btn";
import $api from "@/api/Axios";
import LookData from "./components/LookData.vue";
import type { Response } from "@/types/index";
import { useDataDict } from "@/stores/modules/dataDict";
import type { VxeTableEvents } from "vxe-table";
const REQUESTMETHODOptions = ref<any>([]);
const loading = ref<boolean>(false);
const refVxeTable = ref();
const xToolbar = ref();
nextTick(() => {
    // 将表格和工具栏进行关联
    const $table = refVxeTable.value;
    $table?.connect(xToolbar.value);
});

const options = ref<Array<DictParams>>([]);
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

const fetchDictData = async (parentId: string) => {
    const response: Response = await $api.get(
        `/system/api/web/v1/sysDict/parentType/${parentId}`
    );
    const { code, data } = response;
    if (code === 200) {
        console.log(data);
        options.value = data;
    }
};
async function getList() {
    refVxeTable.value?.clearCheckboxRow();
    loading.value = true;
    const p1 = dataDict.getDictList("OPERATE_STATUS");
    const p2 = dataDict.getDictList("BUSINESS_TYPE");
    const p3 = dataDict.getDictList("OPERATOR_TYPE");
    const { total, records } = await apiGetList(ajaxParams);
    Promise.all([p1, p2, p3]).then(() => {
        response.records = records.map((item) => {
            const statusName = dataDict.getDictListValbyId(
                "OPERATE_STATUS",
                item.status
            );
            const businessTypeName = dataDict.getDictListValbyId(
                "BUSINESS_TYPE",
                item.businessType
            );
            const operatorTypeName = dataDict.getDictListValbyId(
                "OPERATOR_TYPE",
                item.operatorType
            );

            // operatorType
            return Object.assign(
                {
                    statusName: statusName,
                    businessTypeName2: businessTypeName,
                    operatorTypeName2: operatorTypeName,
                },
                item
            );
        });
        // console.log(response.records, "response.records");
        response.total = total;
        loading.value = false;
    });
}

onBeforeMount(async () => {
    // const dataDict = useDataDict();
    // REQUESTMETHODOptions.value = await dataDict.getDictList("REQUEST_METHOD");
    getList();
    fetchDictData("REQUEST_METHOD");
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

    <WrapTableLayout @query="getList()" @reset="reset" more v-loading="loading">
        <template #header>
            <WrapToolbarSearchLayout
                label="请求方式"
                style="margin-right: 0.6vw"
            >
                <!-- <el-select
                    clearable
                    v-model="ajaxParams.requestMethod"
                    placeholder="请选择请求方式"
                >
                    <el-option
                        v-for="item in REQUESTMETHODOptions"
                        :key="item.id"
                        :label="item.dictName"
                        :value="item.dictType"
                    />
                </el-select> -->
                <!-- <GlobalDictSelect
                    parentId="REQUEST_METHOD"
                    v-model="ajaxParams.requestMethod"
                    placeholder="请选择请求方式"
                /> -->
                <el-select
                    v-model="ajaxParams.requestMethod"
                    class="m-2"
                    placeholder="请选择请求方式"
                >
                    <el-option
                        v-for="item in options"
                        :key="item.dictType"
                        :label="item.dictName"
                        :value="item.dictName"
                    />
                </el-select>
            </WrapToolbarSearchLayout>

            <WrapToolbarSearchLayout label="url名称">
                <GlobalSearchInput
                    @query="getList()"
                    v-model="ajaxParams.operUrl"
                    placeholder="请输入请求URL查询"
                    style="margin-right: 0.6vw"
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

        <template #drop>
            <!-- <GlobalSearchInput
                @query="getList()"
                v-model="ajaxParams.userName"
                placeholder="请输入模糊查询内容"
                style="margin-right: 0.6vw"
            ></GlobalSearchInput> -->
        </template>

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

            <vxe-column title="业务类型" field="businessType" sortable>
                <template #default="{ row }">
                    {{ row.businessTypeName2 }}
                </template>
            </vxe-column>

            <vxe-column title="组织名称" field="deptName">
                <template #default="{ row }">
                    {{ row.deptName }}
                </template>
            </vxe-column>

            <vxe-column title="错误消息" field="errorMsg" sortable>
                <template #default="{ row }">
                    {{ row.errorMsg }}
                </template>
            </vxe-column>

            <vxe-column title="返回参数" field="jsonResult" sortable>
                <template #default="{ row }">
                    {{ row.jsonResult }}
                </template>
            </vxe-column>

            <vxe-column title="方法名称" field="method" sortable>
                <template #default="{ row }">
                    {{ row.method }}
                </template>
            </vxe-column>

            <vxe-column title="主机地址" field="operIp" sortable>
                <template #default="{ row }">
                    {{ row.operIp }}
                </template>
            </vxe-column>

            <vxe-column title="操作地点" field="operLocation" sortable>
                <template #default="{ row }">
                    {{ row.operLocation }}
                </template>
            </vxe-column>

            <vxe-column title="操作人员" field="operName" sortable>
                <template #default="{ row }">
                    {{ row.operName }}
                </template>
            </vxe-column>

            <vxe-column title="请求参数" field="operParam" sortable>
                <template #default="{ row }">
                    {{ row.operParam }}
                </template>
            </vxe-column>

            <vxe-column title="操作时间" field="operTime" sortable>
                <template #default="{ row }">
                    {{ row.operTime }}
                </template>
            </vxe-column>

            <vxe-column title="请求URL" field="operUrl" sortable>
                <template #default="{ row }">
                    {{ row.operUrl }}
                </template>
            </vxe-column>

            <vxe-column title="操作类别" field="operatorType" sortable>
                <template #default="{ row }">
                    {{ row.operatorTypeName2 }}
                </template>
            </vxe-column>

            <vxe-column title="请求方式" field="requestMethod" sortable>
                <template #default="{ row }">
                    {{ row.requestMethod }}
                </template>
            </vxe-column>

            <vxe-column title="操作状态" field="status" sortable>
                <template #default="{ row }">
                    {{ row.statusName }}
                </template>
            </vxe-column>

            <vxe-column title="模块标题" field="title" sortable>
                <template #default="{ row }">
                    {{ row.title }}
                </template>
            </vxe-column>

            <vxe-column field="" title="操作">
                <template #default="{ row }">
                    <GlobalAuthElButtonLink
                        :auth="BtnAuth.look"
                        size="small"
                        type="primary"
                        link
                        @click="
                            opObject.show = true;
                            opObject.code = 'look';
                            opObject.id = row.id;
                        "
                    >
                        查看
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
import type { orderByDTOSParams } from "@/types";
import type { DictParams } from "@/components/GlobalDictSelect.vue";
export default defineComponent({
    title: "操作日志记录",
    name: "OperateLog",
});
</script>
