<script setup lang="ts">
import { reactive, ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import $api from "@/api/Axios";

// PTIDSS 数据底座·数据源管理（data_source 台账 + 采集任务触发）
// 参考原型 admin-system.html「数据源」Tab：编码/类型/同步模式/频率/状态台账

interface DataSource {
    id: string;
    sourceCode: string;
    sourceType: string;
    syncMode: string;
    frequency: string;
    status: string;
    lastRunTime?: string | null;
    lastStatus?: string | null;
    recordsCount?: string | number | null;
}

const loading = ref(false);
const list = ref<DataSource[]>([]);

const stats = computed(() => ({
    total: list.value.length,
    online: list.value.filter((d) => d.status === "enabled").length,
    error: list.value.filter((d) => d.status === "error" || d.lastStatus === "error").length,
    collected: list.value.reduce((s, d) => s + (Number(d.recordsCount) || 0), 0),
}));

const typeOptions = [
    { value: "marketing", label: "营销系统" },
    { value: "exchange", label: "交易中心" },
    { value: "weather", label: "气象数据" },
    { value: "file", label: "文件导入" },
    { value: "intel", label: "情报采集" },
];
const modeOptions = [
    { value: "realtime", label: "实时同步" },
    { value: "timed", label: "定时同步" },
];
const statusOptions = [
    { value: "enabled", label: "启用" },
    { value: "disabled", label: "停用" },
    { value: "error", label: "异常" },
];

const typeLabel = (v: string) => typeOptions.find((o) => o.value === v)?.label || v;
const modeLabel = (v: string) => modeOptions.find((o) => o.value === v)?.label || v;

const load = async () => {
    loading.value = true;
    try {
        const res: any = await $api.get(`/data/sources`);
        if (res.code === 0) list.value = res.data || [];
    } finally {
        loading.value = false;
    }
};

// ── 新建数据源 ──
const dialogVisible = ref(false);
const formRef = ref();
const form = reactive<any>({
    sourceCode: "",
    sourceType: "exchange",
    syncMode: "timed",
    frequency: "0 0/30 * * * *",
    status: "enabled",
    connectConfig: "",
});
const rules = {
    sourceCode: [{ required: true, message: "请输入数据源编码", trigger: "blur" }],
    sourceType: [{ required: true, message: "请选择数据源类型", trigger: "change" }],
};

const openCreate = () => {
    Object.assign(form, {
        sourceCode: "",
        sourceType: "exchange",
        syncMode: "timed",
        frequency: "0 0/30 * * * *",
        status: "enabled",
        connectConfig: "",
    });
    dialogVisible.value = true;
};

const submit = async () => {
    await formRef.value.validate();
    const payload: any = { ...form };
    if (!payload.connectConfig) delete payload.connectConfig;
    const res: any = await $api.post(`/data/sources`, payload);
    if (res.code === 0) {
        ElMessage.success(`数据源登记成功（ID ${res.data.id}）`);
        dialogVisible.value = false;
        load();
    }
};

// ── 手动触发采集 ──
const taskTypes = [
    { value: "market", label: "市场行情采集" },
    { value: "weather", label: "气象数据采集" },
    { value: "settlement", label: "结算数据采集" },
];
const collecting = ref(false);
const triggerCollect = async (taskType: string) => {
    collecting.value = true;
    try {
        const res: any = await $api.post(`/data/collect-tasks`, { taskType, force: true });
        if (res.code === 0) {
            ElMessage.success(`采集完成：${taskType} 记录 ${res.data.recordsCount} 条`);
            load();
        }
    } finally {
        collecting.value = false;
    }
};

onMounted(load);
</script>

<template>
    <div class="page">
        <!-- 统计卡（对齐原型 wf-stat） -->
        <el-row :gutter="16" class="stat-row">
            <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                    <div class="stat-label">数据源总数</div>
                    <div class="stat-value">{{ stats.total }}</div>
                    <div class="stat-tip">data_source 台账</div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                    <div class="stat-label">在线数据源</div>
                    <div class="stat-value ok">{{ stats.online }}</div>
                    <div class="stat-tip">status=enabled</div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                    <div class="stat-label">异常数据源</div>
                    <div class="stat-value" :class="{ danger: stats.error > 0 }">{{ stats.error }}</div>
                    <div class="stat-tip">status=error / last_status=error</div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                    <div class="stat-label">累计采集记录</div>
                    <div class="stat-value">{{ stats.collected.toLocaleString() }}</div>
                    <div class="stat-tip">records_count 合计</div>
                </el-card>
            </el-col>
        </el-row>

        <el-card shadow="never">
            <div class="toolbar">
                <span class="tip-text">数据对接台账：营销 / 交易中心 / 气象 / 文件 / 情报双通道建模</span>
                <div>
                    <el-dropdown trigger="click" @command="triggerCollect">
                        <el-button type="success" :loading="collecting">手动采集<i class="el-icon-arrow-down el-icon--right" /></el-button>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item v-for="t in taskTypes" :key="t.value" :command="t.value">
                                    {{ t.label }}
                                </el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                    <el-button type="primary" @click="openCreate">新增数据源</el-button>
                    <el-button @click="load">刷新</el-button>
                </div>
            </div>

            <el-table :data="list" v-loading="loading" border stripe>
                <el-table-column prop="sourceCode" label="数据源编码" width="160">
                    <template #default="{ row }"><span class="mono">{{ row.sourceCode }}</span></template>
                </el-table-column>
                <el-table-column label="类型" width="120">
                    <template #default="{ row }">
                        <el-tag size="small">{{ typeLabel(row.sourceType) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="同步模式" width="110">
                    <template #default="{ row }">{{ modeLabel(row.syncMode) }}</template>
                </el-table-column>
                <el-table-column prop="frequency" label="同步频率" min-width="150">
                    <template #default="{ row }"><span class="mono">{{ row.frequency || '—' }}</span></template>
                </el-table-column>
                <el-table-column label="状态" width="90">
                    <template #default="{ row }">
                        <el-tag
                            :type="row.status === 'enabled' ? 'success' : row.status === 'error' ? 'danger' : 'info'"
                            size="small"
                        >
                            {{ statusOptions.find((o) => o.value === row.status)?.label || row.status }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="lastRunTime" label="最近运行" width="170">
                    <template #default="{ row }">
                        <span class="mono">{{ row.lastRunTime || '—' }}</span>
                        <el-tag v-if="row.lastStatus" :type="row.lastStatus === 'success' ? 'success' : 'danger'" size="small" style="margin-left: 4px">
                            {{ row.lastStatus }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="记录数" width="100">
                    <template #default="{ row }">
                        <span class="mono">{{ Number(row.recordsCount)?.toLocaleString?.() || '—' }}</span>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="dialogVisible" title="新增数据源" width="560px" destroy-on-close>
            <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
                <el-form-item label="数据源编码" prop="sourceCode">
                    <el-input v-model="form.sourceCode" placeholder="如 SRC-EXCHANGE-01" />
                </el-form-item>
                <el-form-item label="数据源类型" prop="sourceType">
                    <el-select v-model="form.sourceType" style="width: 100%">
                        <el-option v-for="o in typeOptions" :key="o.value" :value="o.value" :label="o.label" />
                    </el-select>
                </el-form-item>
                <el-form-item label="同步模式">
                    <el-select v-model="form.syncMode" style="width: 100%">
                        <el-option v-for="o in modeOptions" :key="o.value" :value="o.value" :label="o.label" />
                    </el-select>
                </el-form-item>
                <el-form-item label="同步频率">
                    <el-input v-model="form.frequency" placeholder="cron 表达式，如 0 0/30 * * * *" />
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="form.status" style="width: 100%">
                        <el-option v-for="o in statusOptions" :key="o.value" :value="o.value" :label="o.label" />
                    </el-select>
                </el-form-item>
                <el-form-item label="连接配置">
                    <el-input v-model="form.connectConfig" type="textarea" :rows="3" placeholder='可选，JSON 文本，如 {"host":"..."}；缺省 {}' />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submit">保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<style scoped lang="scss">
.page {
    padding: 16px;

    .stat-row {
        margin-bottom: 16px;
    }
    .stat-card {
        .stat-label {
            font-size: 13px;
            color: #86909c;
        }
        .stat-value {
            font-size: 30px;
            font-weight: 700;
            color: #2f6fed;
            line-height: 1.4;

            &.ok {
                color: #2e9e5b;
            }
            &.danger {
                color: #d64545;
            }
        }
        .stat-tip {
            font-size: 12px;
            color: #a9aeb8;
        }
    }
    .toolbar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .tip-text {
            color: #86909c;
            font-size: 13px;
        }
    }
    .mono {
        font-family: Consolas, monospace;
    }
}
</style>
