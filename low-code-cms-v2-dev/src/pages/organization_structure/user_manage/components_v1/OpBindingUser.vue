<script setup lang="ts">
import { reactive } from "vue";
import opData from "./op/opData.vue";
import { useOpListData } from "./hooks/useListData";

const props = defineProps<{
    id: string;
    sysUserVO: any;
}>();

const rowSelect: any = reactive(null);

const {
    refVxeTableDom,
    xToolbarDom,
    ajaxParams,
    response,
    loading,
    opObject,
    close,
    reset,
    fetchList,
    fetchDel,
    phone
} = useOpListData(props);

const emit = defineEmits(["query", "close"]);
</script>

<template>
    <opData
        v-if="opObject.code === 'add' || opObject.code === 'edit'"
        :title="opObject.code === 'add' ? '添加' : '编辑'"
        :id="props.id"
        :phones="phone"
        :opObject="opObject"
        :rowSelect="rowSelect"
        v-model="opObject.show"
        @close="close"
        @query="
            ajaxParams.current = 1;
            fetchList();
        "
    >
    </opData>

    <GlobalElDialog :="$attrs" @close="emit('close')" width="50vw">
        <!-- {{ props.sysUserVO }} -->

        <WrapTableLayout
            style="height: 8vh"
            @query="fetchList()"
            @reset="reset"
        >
            <template #header>
                <WrapToolbarSearchLayout label="第三方标识">
                    <GlobalSearchInput
                        @query="fetchList()"
                        v-model="ajaxParams.username"
                        placeholder="请输入第三方标识查询"
                        style="margin-right: 0.6vw"
                    ></GlobalSearchInput>
                </WrapToolbarSearchLayout>

                <!-- <WrapToolbarSearchLayout label="联系方式">
                    <GlobalSearchInput
                        @query="getList()"
                        v-model="ajaxParams.phone"
                        placeholder="请输入联系方式查询"
                    ></GlobalSearchInput>
                </WrapToolbarSearchLayout> -->
            </template>

            <template #toolbar>
                <vxe-toolbar ref="xToolbar">
                    <template #buttons>
                        <el-button
                            type="primary"
                            @click.stop="
                                opObject.show = true;
                                opObject.code = 'add';
                            "
                            >添加</el-button
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
                    </template>

                    <template #tools> </template>
                </vxe-toolbar>
            </template>
        </WrapTableLayout>

        <vxe-table
            ref="refVxeTableDom"
            align="center"
            :data="response.records"
            :row-config="{ isCurrent: true, isHover: true }"
            size="medium"
            border="inner"
            show-overflow
            height="300"
            :auto-resize="true"
            :sync-resize="true"
        >
            <vxe-column
                title="本系统username"
                field="loginUsername"
            ></vxe-column>
            <vxe-column title="系统标识" field="sysTarget"></vxe-column>
            <vxe-column title="第三方标识" field="username"></vxe-column>
            <vxe-column title="账号备注" field="content"></vxe-column>
            <vxe-column title="操作" width="110" fixed="right">
                <template #default="{ row }">
                    <!-- <el-button
                        size="small"
                        type="primary"
                        link
                        @click="
                            rowSelect = row;
                            opObject.id = row.id;
                            opObject.show = true;
                            opObject.code = 'edit';
                        "
                    >
                        编辑
                    </el-button> -->
                    <el-button
                        size="small"
                        type="danger"
                        link
                        @click="fetchDel(row.id)"
                    >
                        删除
                    </el-button>
                </template>
            </vxe-column>
        </vxe-table>

        <div class="GlobalElPagination-11">
            <GlobalElPagination
                v-model:currentPage="ajaxParams.current"
                v-model:page-size="ajaxParams.size"
                :total="response.total"
                @size-change="fetchList"
                @current-change="fetchList"
            />
        </div>
    </GlobalElDialog>
</template>

<style scoped lang="scss">
.GlobalElPagination-11 {
    display: flex;
    align-items: center;
    justify-content: center;
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({
    title: "用户绑定",
    name: "OpBindingUser",
});
</script>
