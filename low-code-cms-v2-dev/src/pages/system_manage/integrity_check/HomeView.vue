<script setup lang="ts">
import { reactive, onBeforeMount, ref, nextTick } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import {
    CirclePlus, //添加
    Delete, // 删除
} from "@element-plus/icons-vue";
import { apiGetList, apiDelete, initParams, apiValid } from "./api";
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
    // getSysConfig();
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

const selectList = [
    { name: '用户', val: '1' },
    { name: 'usbKey', val: '2' },
    { name: '角色', val: '3' },
    { name: '登录日志', val: '4' },
    { name: '操作日志', val: '5' },
    { name: '系统配置', val: '6' },
    { name: '从业人员信息', val: '7' },
    { name: '运营车辆信息', val: '8' },
    { name: '业户信息', val: '9' },
    { name: '航道', val: '10' },
    { name: '拦截线', val: '11' },
    { name: '船闸', val: '12' },
    { name: '停泊区', val: '13' }
]
const searchTime = ref(null)
const getTimeVal = () => {
    console.log(searchTime.value)
    if (searchTime.value) {
        ajaxParams.startTime = searchTime.value[0]
        ajaxParams.endTime = searchTime.value[1]
    } else {
        ajaxParams.startTime = ''
        ajaxParams.endTime = ''
    }
}
const handleValid = async (row:any) => {
    const getUrl = () => {
        // /api/web/v1/check/1
        const option:any = selectList.find((item:any) => item.name === row.name)
        const val = Number(option.val)
        if (!val) {
            // 
            return false
        }
        if ([1,2,3,4,5,16].includes(val)) {
            return `/system/api/web/v1/check/${val}`
        } else if (val == 10) {
            return `/basics/api/web/v1/check/${val}`
        } else if ([7,8,9,11,12,13].includes(val)) {
            return `/traffic/api/web/v1/check/${val}`
        }
    }
    const url = getUrl()
    const bool = await apiValid(url);
    bool && getList();
}
</script>

<template>
    <OperatData
        v-if="opObject.code === 'valid'"
        :title="'校验'"
        :id="opObject.id"
        v-model="opObject.show"
        @query="getList()"
        @close="close"
    />
    <WrapTableLayout @query="getList()" @reset="reset" more>
        <template #header>
            <WrapToolbarSearchLayout label="名称">
                <el-select v-model="ajaxParams.name" clearable>
                    <el-option
                        v-for="item in selectList"
                        :key="item.val"
                        :label="item.name"
                        :value="item.name">
                    </el-option>
                </el-select>
            </WrapToolbarSearchLayout>
            <WrapToolbarSearchLayout label="时间范围">
                <el-date-picker
                    v-model="searchTime"
                    type="datetimerange"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    range-separator="至"
                    start-placeholder="选择开始时间"
                    end-placeholder="选择结束时间"
                    @change="getTimeVal"
                />
            </WrapToolbarSearchLayout>
        </template>

        <template #drop> </template>

        <template #toolbar>
            <vxe-toolbar ref="xToolbar" custom>
                <template #buttons>
                   
                    <GlobalAuthElButtonLink
                        auth="valid"
                        type="success"
                        @click="
                            opObject.id = '';
                            opObject.show = true;
                            opObject.code = 'valid';
                        "
                    >
                        校验
                    </GlobalAuthElButtonLink> 
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

            <vxe-column title="名称" field="name"  />
            <vxe-column title="验证时间" field="checkTime"  >
                <template #default="{ row }">
                    {{ row.checkTime ? row.checkTime.substring(0,19).replace('T', ' ') : '--' }}
                </template>
            </vxe-column>
            <vxe-column title="数量" field="errorNum" sortable />
            <!-- <vxe-column title="参数键名" field="configKey" />
            <vxe-column title="系统内置" field="configType" />
            <vxe-column title="参数键值" field="configValue" /> -->
            <vxe-column title="备注" field="checkInfo"  >
                <template #default="{ row }">
                    {{ row.checkInfo ? JSON.parse(JSON.stringify(row.checkInfo)) : '--' }}
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
