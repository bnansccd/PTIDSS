<script setup lang="ts">
import { reactive, onBeforeMount, ref, nextTick } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import {
    CirclePlus, //添加
    Delete, // 删除
} from "@element-plus/icons-vue";

import { apiGetList, apiDelete, initParams } from "./api";
import BtnAuth from "./auth_btn";
import type { Response } from "@/types";
import OperatData from "./components/OperatData.vue";
import OperatTitle from "./components/OperatTitle.vue";
import OperatSysParam from "./components/OperatSysParam.vue";
import OperatSysLogo from "./components/OperatSysLogo.vue";
import OperatSysTheme from "./components//OperatSysTheme.vue";
import LookData from "./components/LookData.vue";
import type { VxeTableEvents } from "vxe-table";
const refVxeTable = ref();
const xToolbar = ref();
nextTick(() => /* 将表格和工具栏进行关联*/ {
    const $table = refVxeTable.value;
    $table?.connect(xToolbar.value);
});

const opInit = {
    show: false,
    code: "init", // "title" "edit" "add"
    id: "0", //id为0时表示添加
    row: {} as ConfigParams,
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
    // 查询更新系统参数
    getSysConfig();
}

const userStore = useUserStore();
const getSysConfig = async () => {
    const res: Response = (await userStore.getSysConfig()) as Response;
    const records = (res.data as Array<ConfigParams>) || [];
    // let tempArr: Array<ConfigParams> = [];
    // records.forEach((item) => {
    //     tempArr = [...tempArr, ...(item.list as Array<ConfigParams>)];
    // });
    // records = tempArr;
    localStorage.setItem("system_config", JSON.stringify(records));
    const sysConfig = useSysConfigStore();
    sysConfig.systemConfig = records;
};
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
    }).then(async () => {
        const bool = await apiDelete(Array.isArray(val) ? ids : val);
        bool && getList();
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
        v-if="opObject.code === 'add' || (opObject.code === 'config_key')"
        :title="opObject.code === 'add' ? '添加' : '编辑'"
        :id="opObject.id"
        v-model="opObject.show"
        @query="getList()"
        @close="close"
    />
    <OperatTitle
        v-if="
            opObject.code === 'system_title' ||
            opObject.code === 'system_body' ||
            opObject.code === 'system_size' ||
            opObject.code === 'system_titile_color' ||
            opObject.code === 'system_bread' ||
            opObject.code === 'system_label'
        "
        :title="`编辑${opObject.row.configName}`"
        :id="opObject.id"
        :row="opObject.row"
        v-model="opObject.show"
        @query="getList()"
        @close="close"
    />
    <!-- <OperatSysParam
        v-if="opObject.code === 'system'"
        :title="`编辑${opObject.row.configName}`"
        :id="opObject.id"
        v-model="opObject.show"
        @query="getList()"
        @close="close"
    /> -->
    <OperatSysLogo
        v-if="
            opObject.code === 'system_logo' ||
            opObject.code === 'system_little_logo'
        "
        :title="`编辑${opObject.row.configName}`"
        :id="opObject.id"
        :row="opObject.row"
        v-model="opObject.show"
        @query="getList()"
        @close="close"
    />
    <OperatSysTheme
        v-if="opObject.code === 'system_theme_color'"
        :title="`编辑${opObject.row.configName}`"
        :id="opObject.id"
        :row="opObject.row"
        v-model="opObject.show"
        @query="getList()"
        @close="close"
    />

    <WrapTableLayout @query="getList()" @reset="reset" more>
        <template #header>
            <WrapToolbarSearchLayout label="参数名称">
                <GlobalSearchInput
                    @query="getList()"
                    v-model="ajaxParams.configName"
                    placeholder="请输入参数名称查询"
                />
            </WrapToolbarSearchLayout>
        </template>

        <template #drop> </template>

        <template #toolbar>
            <vxe-toolbar ref="xToolbar" custom>
                <template #buttons>
                    <GlobalAuthElButton
                        auth="add"
                        type="primary"
                        @click="
                            opObject.show = true;
                            opObject.code = 'add';
                        "

                        >添加</GlobalAuthElButton
                    >

                    <!-- <GlobalAuthElButtonLink
                        :auth="BtnAuth.del"
                        type="danger"

                        @click="del([])"
                        >删除</GlobalAuthElButtonLink
                    > -->
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
            :sort-config="{
                multiple: true,
                chronological: false,
            }"
            @sort-change="sortChangeEvent"
        >
            <!-- <vxe-column type="checkbox" width="45" /> -->

            <vxe-column type="seq" title="序号" width="50" />

            <vxe-column title="参数名称" field="configName" sortable />
            <vxe-column title="创建时间" field="createTime" sortable />
            <vxe-column title="修改时间" field="modifyTime" sortable />
            <!-- <vxe-column title="参数键名" field="configKey" />
            <vxe-column title="系统内置" field="configType" />
            <vxe-column title="参数键值" field="configValue" /> -->
            <vxe-column title="备注" field="remarks" sortable />

            <vxe-column title="操作" field="" width="160">
                <template #default="{ row }">
                    <GlobalAuthElButtonLink
                        auth="look"
                        link
                        v-if="row.basic == 1"
                        size="small"
                        @click="
                            opObject.id = row.id;
                            opObject.show = true;
                            opObject.code = 'look';
                        "
                    >
                        查看
                    </GlobalAuthElButtonLink>
                    <GlobalAuthElButtonLink
                        :auth="BtnAuth.edit"
                        size="small"
                        type="primary"
                        link
                        @click="
                            opObject.id = row.id;
                            opObject.show = true;
                            opObject.code = row.configKey;
                            opObject.row = row;
                        "
                    >
                        编辑
                    </GlobalAuthElButtonLink>


                    <GlobalAuthElButtonLink
                        auth="del"
                        size="small"
                        type="danger"
                        v-if="row.basic == 1"
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
import { useUserStore } from "@/stores/modules/user";
import type { ConfigParams, orderByDTOSParams } from "@/types";
import { useSysConfigStore } from "@/stores/modules/sysConfig";
export default defineComponent({
    title: "参数配置",
    name: "ParamsConfig",
});
</script>
