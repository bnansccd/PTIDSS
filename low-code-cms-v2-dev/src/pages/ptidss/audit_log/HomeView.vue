<script setup lang="ts">
import { reactive, ref, onMounted } from "vue";
import $api from "@/api/Axios";

// PTIDSS 审计日志（DDL 10.3 audit_log，等保三级：操作留痕、关键操作前后快照、按省检索）

interface LogRow {
    id: number;
    traceId?: string;
    userId?: number;
    action: string;
    targetType?: string;
    targetId?: string;
    beforeSnapshot?: string;
    afterSnapshot?: string;
    ip?: string;
    userAgent?: string;
    result: string;
    regionCode?: string;
    createdAt?: string;
}

const loading = ref(false);
const list = ref<LogRow[]>([]);
const total = ref(0);
const pageQuery = reactive({
    pageNum: 1,
    pageSize: 10,
    action: "",
    username: "",
    regionCode: "",
    result: "",
});

const actionOptions = [
    "login",
    "logout",
    "user_create",
    "user_update",
    "user_delete",
    "user_reset_password",
    "role_create",
    "role_update",
    "role_delete",
    "role_permission_save",
    "permission_create",
    "permission_update",
    "permission_delete",
    "region_create",
    "region_update",
    "region_delete",
];

const actionNameMap: Record<string, string> = {
    login: "登录",
    logout: "登出",
    user_create: "创建用户",
    user_update: "更新用户",
    user_delete: "删除用户",
    user_reset_password: "重置密码",
    role_create: "创建角色",
    role_update: "更新角色",
    role_delete: "删除角色",
    role_permission_save: "保存角色权限",
    permission_create: "创建权限",
    permission_update: "更新权限",
    permission_delete: "删除权限",
    region_create: "创建区域",
    region_update: "更新区域",
    region_delete: "删除区域",
};

const resultOptions = [
    { value: "success", label: "成功", type: "success" },
    { value: "fail", label: "失败", type: "danger" },
];

// ---- 详情 ----
const detailVisible = ref(false);
const detail = ref<LogRow | null>(null);
const detailLoading = ref(false);

const load = async () => {
    loading.value = true;
    try {
        const res: any = await $api.get(`/admin/logs`, { params: { ...pageQuery } });
        if (res.code === 0) {
            list.value = res.data.records || [];
            total.value = res.data.total || 0;
        }
    } finally {
        loading.value = false;
    }
};

const openDetail = async (row: LogRow) => {
    detailLoading.value = true;
    detailVisible.value = true;
    try {
        const res: any = await $api.get(`/admin/logs/${row.id}`);
        detail.value = res.code === 0 ? res.data : null;
    } finally {
        detailLoading.value = false;
    }
};

const formatSnapshot = (s?: string) => {
    if (!s) return "—";
    try {
        return JSON.stringify(JSON.parse(s), null, 2);
    } catch (e) {
        return s;
    }
};

const onPageChange = () => load();

onMounted(load);
</script>

<template>
    <div class="page">
        <el-card shadow="never">
            <div class="toolbar">
                <el-form inline @submit.prevent>
                    <el-form-item label="动作">
                        <el-select
                            v-model="pageQuery.action"
                            clearable
                            filterable
                            allow-create
                            default-first-option
                            placeholder="全部"
                            style="width: 180px"
                        >
                            <el-option v-for="a in actionOptions" :key="a" :value="a" :label="`${a}（${actionNameMap[a] || ''}）`" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="操作人">
                        <el-input
                            v-model="pageQuery.username"
                            placeholder="用户名"
                            clearable
                            style="width: 140px"
                            @keyup.enter="pageQuery.pageNum = 1; load()"
                        />
                    </el-form-item>
                    <el-form-item label="区域">
                        <el-input
                            v-model="pageQuery.regionCode"
                            placeholder="如 CN-32"
                            clearable
                            style="width: 120px"
                            @keyup.enter="pageQuery.pageNum = 1; load()"
                        />
                    </el-form-item>
                    <el-form-item label="结果">
                        <el-select v-model="pageQuery.result" clearable placeholder="全部" style="width: 110px">
                            <el-option v-for="o in resultOptions" :key="o.value" :value="o.value" :label="o.label" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="pageQuery.pageNum = 1; load()">查询</el-button>
                    </el-form-item>
                </el-form>
            </div>

            <el-table :data="list" v-loading="loading" border stripe>
                <el-table-column prop="createdAt" label="操作时间" width="170" />
                <el-table-column prop="username" label="操作人" width="110" />
                <el-table-column label="动作" width="170">
                    <template #default="{ row }">
                        <el-tag size="small">{{ actionNameMap[row.action] || row.action }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="targetType" label="目标类型" width="110" />
                <el-table-column prop="targetId" label="目标ID" width="120" />
                <el-table-column prop="regionCode" label="区域" width="90">
                    <template #default="{ row }">
                        <span v-if="row.regionCode">{{ row.regionCode }}</span>
                        <span v-else class="muted">平台级</span>
                    </template>
                </el-table-column>
                <el-table-column label="结果" width="80">
                    <template #default="{ row }">
                        <el-tag :type="row.result === 'success' ? 'success' : 'danger'" size="small">
                            {{ row.result === "success" ? "成功" : "失败" }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="ip" label="IP" width="130" />
                <el-table-column label="操作" width="80" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="openDetail(row)">详情</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <el-pagination
                class="pager"
                v-model:current-page="pageQuery.pageNum"
                v-model:page-size="pageQuery.pageSize"
                :total="total"
                :page-sizes="[10, 20, 50]"
                layout="total, sizes, prev, pager, next"
                @change="onPageChange"
            />
        </el-card>

        <el-dialog v-model="detailVisible" title="审计日志详情" width="720px" destroy-on-close>
            <div v-loading="detailLoading">
                <el-descriptions v-if="detail" :column="2" border>
                    <el-descriptions-item label="操作时间">{{ detail.createdAt }}</el-descriptions-item>
                    <el-descriptions-item label="操作人ID">{{ detail.userId ?? "—" }}</el-descriptions-item>
                    <el-descriptions-item label="动作">
                        {{ actionNameMap[detail.action] || detail.action }}（{{ detail.action }}）
                    </el-descriptions-item>
                    <el-descriptions-item label="结果">
                        <el-tag :type="detail.result === 'success' ? 'success' : 'danger'" size="small">
                            {{ detail.result === "success" ? "成功" : "失败" }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="目标类型">{{ detail.targetType || "—" }}</el-descriptions-item>
                    <el-descriptions-item label="目标ID">{{ detail.targetId || "—" }}</el-descriptions-item>
                    <el-descriptions-item label="归属区域">{{ detail.regionCode || "平台级" }}</el-descriptions-item>
                    <el-descriptions-item label="客户端IP">{{ detail.ip || "—" }}</el-descriptions-item>
                    <el-descriptions-item label="链路追踪ID" :span="2">{{ detail.traceId || "—" }}</el-descriptions-item>
                </el-descriptions>
                <template v-if="detail">
                    <div class="snapshot-block">
                        <div class="snapshot-title">操作前快照（beforeSnapshot）</div>
                        <pre class="snapshot-body">{{ formatSnapshot(detail.beforeSnapshot) }}</pre>
                    </div>
                    <div class="snapshot-block">
                        <div class="snapshot-title">操作后快照（afterSnapshot）</div>
                        <pre class="snapshot-body">{{ formatSnapshot(detail.afterSnapshot) }}</pre>
                    </div>
                </template>
            </div>
            <template #footer>
                <el-button @click="detailVisible = false">关闭</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<style scoped lang="scss">
.page {
    padding: 16px;

    .toolbar {
        margin-bottom: 12px;
    }

    .muted {
        color: #a8abb2;
    }

    .snapshot-block {
        margin-top: 14px;

        .snapshot-title {
            font-weight: 600;
            color: #1f3b6b;
            margin-bottom: 6px;
        }

        .snapshot-body {
            margin: 0;
            padding: 10px;
            background: #f5f7fa;
            border: 1px solid #e4e7ed;
            border-radius: 4px;
            max-height: 260px;
            overflow: auto;
            font-size: 12px;
            white-space: pre-wrap;
            word-break: break-all;
        }
    }
}
</style>
