<script setup lang="ts">
import { reactive, onBeforeMount, ref, nextTick } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import $api from "@/api/Axios";
import {
    CirclePlus, //添加
    Delete, // 删除
    Open, // 启用
    TurnOff, // 停用
} from "@element-plus/icons-vue";

import {
    apiGetList,
    apiDelete,
    apiStatus,
    apiReastPassword,
    initParams,
    importUser
} from "./api";
import BtnAuth from "./auth_btn";

import OpBindingUser from "./components_v1/OpBindingUser.vue";

import ConfigUserRole from "./components/ConfigUserRole.vue";
import OperatData from "./components/OperatData.vue";
import BindUser from "./components/BindUser.vue";
import LookData from "./components/LookData.vue";
import SecureLock from "./components/SecureLock.vue";
import type { VxeTableEvents } from "vxe-table";
const refVxeTable = ref();
const xToolbar = ref();
const tenantCode = localStorage.getItem("sysTenantVO") ? JSON.parse(localStorage.getItem("sysTenantVO")).code : "";
const appName = ref(tenantCode == 'NJ' ? '巴政通' : "广政通");
nextTick(() => /* 将表格和工具栏进行关联*/ {
    const $table = refVxeTable.value;
    $table?.connect(xToolbar.value);
});

const sysUserVO = reactive({
    id: "",
});

const opInit = {
    show: false,
    code: "init", // "look" "edit" "add" "binding"
    id: "0", //id为0时表示添加
};
const opObject = reactive({
    ...opInit,
});

const loading = ref(false);
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
    refVxeTable.value?.clearCheckboxRow(); //清空多选
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
    console.log("del");
    let ids: Array<string>;
    if (Array.isArray(val)) {
        ids = refVxeTable.value
            ?.getCheckboxRecords(true)
            .map((item: { id: any }) => item.sysUserVO.id);
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
        bool = await apiStatus(val);
    }
    bool && getList();
};

const resetPassword = async (id: string) => {
    ElMessageBox.confirm("此操作将重置选中用户的密码, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        center: true,
    })
        .then(async () => {
            await apiReastPassword(id);
        })
        .catch(() => {});
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

const isSync = ref(false)
const importSyncOrg = async () => {
    isSync.value = true
    const bool = await importUser();
    isSync.value = false
    bool && getList();
}

const btnLoading = ref(false)
const visibleApp = async (api:string) => {
    btnLoading.value = true
    const ids:any = []
    refVxeTable.value.getCheckboxRecords().forEach((item: any) => {
        ids.push(item.sysUserVO.id);
    });
    await $api.post(`/roadSystem/api/web/v1/gzt/getOpenIdsMobileMd5s`, ids).then(async (res:any) => {
        if (res.code == 200) {
            console.log(JSON.parse(res.data))
            const data = res.data ? JSON.parse(res.data) : null
            const arr = (data?.data?.users || []).map((item:any) => {
                return item.openUserId
            })
            if (arr.length) {
                await $api.post(api, {
                    openUserIds: arr,
                    appKey: 'ETUffku7BE9GivX2hxZ8'
                }).then((result:any) => {
                    if (result.code == 200) {
                        getList();
                    }
                }) 
            } else {
                ElMessage({
                    message: '暂无用户',
                    type: 'warning'
                })
            }
             
        }
    })
    btnLoading.value = false
}
const handleVisable = async (v:number) => {
    ElMessageBox.confirm("请选择显示或者隐藏广政通APP数字交通菜单", "提示", {
        confirmButtonText: "显示",
        cancelButtonText: "隐藏",
        type: "warning",
        center: true,
    })
        .then(async () => {
            visibleApp('/roadSystem/api/web/v1/gzt/setUserVisible')
        })
        .catch(async () => {
            visibleApp('/roadSystem/api/web/v1/gzt/setUserInvisible')
        });
    
    
}
</script>

<template>
    <OpBindingUser
        title="绑定设置"
        v-if="opObject.code === 'binding'"
        :id="opObject.id"
        :sysUserVO="sysUserVO"
        v-model="opObject.show"
        @close="close"
        @query="
            ajaxParams.current = 1;
            getList();
        "
    >
    </OpBindingUser>

    <ConfigUserRole
        v-if="opObject.code === 'role'"
        :id="opObject.id"
        v-model="opObject.show"
        @close="close"
        @query="
            ajaxParams.current = 1;
            getList();
        "
    />
    <BindUser
        title="同步用户"
        v-if="opObject.code === 'bind'"
        :id="opObject.id"
        v-model="opObject.show"
        @close="close"
    />

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
    
    <SecureLock
        v-if="opObject.code === 'lock'"
        title="解除用户账号锁定"
        :id="opObject.id"
        v-model="opObject.show"
        @query="getList()"
        @close="close"
    />


    <div class="user-main-box">
        <DepartSelect
            @change="
                (departId) => {
                    ajaxParams.departId = departId;
                    getList();
                }
            "
        />
        <WrapTableLayout
            class="user-content-box"
            @query="getList()"
            @reset="reset"
            more
        >
            <template #header>
                <WrapToolbarSearchLayout label="用户名">
                    <GlobalSearchInput
                        @query="getList()"
                        v-model="ajaxParams.realName"
                        placeholder="请输入用户名查询"
                        style="margin-right: 0.6vw"
                    ></GlobalSearchInput>
                </WrapToolbarSearchLayout>

                <!-- 25/8/26 暂时注释 -->
                <WrapToolbarSearchLayout label="账号名称">
                    <GlobalSearchInput
                        @query="getList()"
                        v-model="ajaxParams.username"
                        placeholder="请输入账号查询"
                        style="margin-right: 0.6vw"
                    ></GlobalSearchInput>
                </WrapToolbarSearchLayout>

                <WrapToolbarSearchLayout label="联系方式">
                    <GlobalSearchInput
                        @query="getList()"
                        v-model="ajaxParams.phone"
                        placeholder="请输入联系方式查询"
                    ></GlobalSearchInput>
                </WrapToolbarSearchLayout>
                <!-- 25/8/26 暂时注释结束A -->
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
                        <GlobalAuthElButton
                            :auth="BtnAuth.importData"
                            type="primary"
                            :loading="isSync"
                            @click.stop="
                                opObject.show = true;
                                opObject.code = 'bind';
                            "
                            >同步{{appName}}用户</GlobalAuthElButton
                        >   
                        <GlobalAuthElButton
                            :auth="BtnAuth.lock"
                            type="primary"
                            @click.stop="
                                opObject.show = true;
                                opObject.code = 'lock';
                            "
                            >解除用户锁定</GlobalAuthElButton
                        >
                        <GlobalAuthElButton
                            auth="visible"
                            type="primary"
                            :loading="btnLoading"
                            @click.stop="handleVisable(1)"
                            >显示/隐藏{{appName}}APP数字交通</GlobalAuthElButton
                        >
                        <!-- <GlobalAuthElButton
                            auth="visible"
                            type="primary"
                            :loading="btnLoading"
                            @click.stop="handleVisable(0)"
                            >隐藏广政通APP数字交通</GlobalAuthElButton
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
                :loading="loading"
                height="auto"
                class="mytable-scrollbar"
                :auto-resize="true"
                :sync-resize="true"
                :column-config="{ resizable: true }"
                :sort-config="{
                    multiple: true,
                    chronological: false,
                }"
                @sort-change="sortChangeEvent"
            >
                <vxe-column
                    type="checkbox"
                    width="45"
                    title=""
                    fixed="left"
                ></vxe-column>
                <vxe-column type="seq" title="序号" width="50"></vxe-column>

                <vxe-column
                    title="真实姓名"
                    field="realName"
                    sortable
                    width="150"
                >
                    <template #default="{ row }">
                        {{ row.sysUserVO.realName }}
                    </template>
                </vxe-column>
                <vxe-column title="账号" field="username" sortable width="150">
                    <template #default="{ row }">
                        {{ row.sysUserVO.username }}
                    </template>
                </vxe-column>

                <vxe-column title="联系方式" field="phone" sortable width="150">
                    <template #default="{ row }">
                        {{ row.sysUserVO.phone || "--" }}
                    </template>
                </vxe-column>

                <vxe-column title="角色" field="sysRoleVOS" width="150">
                    <template #default="{ row }">
                        {{
                            row.sysRoleVOS.map((item: any) => item.roleName) +
                            ""
                        }}
                    </template>
                </vxe-column>

                <vxe-column title="组织" field="sysDepartVO" width="150">
                    <template #default="{ row }">
                        {{
                            (row.sysDepartVO && row.sysDepartVO.departName) ||
                            "--"
                        }}
                    </template>
                </vxe-column>

                <vxe-column title="岗位" field="sysPostVOS" width="150">
                    <template #default="{ row }">
                        {{
                            Array.isArray(row.sysPostVOS) &&
                            row.sysPostVOS.length > 0
                                ? row.sysPostVOS
                                      .map((item: any) => item.postName)
                                      .toString()
                                : "--"
                        }}
                    </template>
                </vxe-column>

                <!-- <vxe-column title="头像" field="headUrl">
                    <template #default="{ row }">
                        {{ row.headUrl }}
                    </template>
                </vxe-column> -->

                <vxe-column
                    title="登录次数"
                    field="loginTimes"
                    width="100"
                    sortable
                >
                    <template #default="{ row }">
                        {{ row.sysUserVO.loginTimes }}
                    </template>
                </vxe-column>

                <vxe-column
                    title="最后登录ip"
                    field="lastLoginIp"
                    width="150"
                    sortable
                >
                    <template #default="{ row }">
                        {{ row.sysUserVO.lastLoginIp }}
                    </template>
                </vxe-column>

                <vxe-column
                    title="最后登录时间"
                    field="lastLoginTime"
                    width="150"
                    sortable
                >
                    <template #default="{ row }">
                        {{ row.sysUserVO.lastLoginTime }}
                    </template>
                </vxe-column>

                <vxe-column
                    title="创建时间"
                    field="createTime"
                    width="150"
                    sortable
                >
                    <template #default="{ row }">
                        {{ row.sysUserVO.createTime }}
                    </template>
                </vxe-column>
                <vxe-column
                    title="修改时间"
                    field="modifyTime"
                    width="150"
                    sortable
                >
                    <template #default="{ row }">
                        {{ row.sysUserVO.modifyTime }}
                    </template>
                </vxe-column>

                <!-- <vxe-column title="状态" field="status" width="50">
                    <template #default="{ row }">
                        {{ row.status === "0" ? "启用" : "停用" }}
                    </template>
                </vxe-column> -->

                <vxe-column title="启用停用" width="100">
                    <!-- 启用停用(0启用1停用) -->
                    <template #default="{ row }">
                        <GlobalElSwitch
                            @change="changeStatus(row.sysUserVO.id)"
                            v-model="row.sysUserVO.status"
                            active-value="0"
                            inactive-value="1"
                        />
                    </template>
                </vxe-column>

                <vxe-column field="" title="操作" width="250" fixed="right">
                    <template #default="{ row }">
                        <GlobalAuthElButtonLink
                            :auth="BtnAuth.del"
                            size="small"
                            link
                            @click="
                                Object.assign(sysUserVO, row.sysUserVO)
                                opObject.id = row.sysUserVO.id;
                                opObject.show = true;
                                opObject.code = 'binding';
                            "
                        >
                            绑定设置
                        </GlobalAuthElButtonLink>

                        <GlobalAuthElButtonLink
                            :auth="BtnAuth.reset"
                            size="small"
                            type="warning"
                            link
                            @click="resetPassword(row.sysUserVO.id)"
                        >
                            <!-- <el-icon>
                                <RefreshRight />
                            </el-icon> -->
                            重置密码
                        </GlobalAuthElButtonLink>

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
                            type="primary"
                            link
                            @click="
                                opObject.id = row.sysUserVO.id;
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
                            @click="del(row.sysUserVO.id)"
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
    </div>
</template>

<style lang="scss" scoped>
.user-main-box {
    width: 100%;
    height: 100%;
    display: flex;
}

.user-content-box {
    flex: 1;
}

/*滚动条整体部分*/
.mytable-scrollbar ::-webkit-scrollbar {
    width: 10px;
    height: 10px;
}

/*滚动条的轨道*/
.mytable-scrollbar ::-webkit-scrollbar-track {
    background-color: #ffffff;
}

/*滚动条里面的小方块，能向上向下移动*/
.mytable-scrollbar ::-webkit-scrollbar-thumb {
    background-color: #bfbfbf;
    border-radius: 5px;
    border: 1px solid #f1f1f1;
    box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.3);
}

.mytable-scrollbar ::-webkit-scrollbar-thumb:hover {
    background-color: #a8a8a8;
}

.mytable-scrollbar ::-webkit-scrollbar-thumb:active {
    background-color: #787878;
}

/*边角，即两个滚动条的交汇处*/
.mytable-scrollbar ::-webkit-scrollbar-corner {
    background-color: #ffffff;
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
import DepartSelect from "./components/DepartSelect.vue";
import type { orderByDTOSParams } from "@/types";
export default defineComponent({
    title: "用户管理",
    name: "UserManage",
    components: { DepartSelect },
});
</script>
