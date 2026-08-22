<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import $api from "@/api/Axios";

// PTIDSS 结算管理（对齐 OpenAPI V1.0 /settlement/** 与 /ocr/**：结算记录/核对引擎/差异工单/OCR 识别）

const activeTab = ref("records");
const loading = ref(false);

// ---- 结算记录 ----
const list = ref<any[]>([]);
const total = ref(0);
const pageQuery = reactive({ pageNo: 1, pageSize: 10, period: "2026-08", source: "" });

const sourceOptions = [
    { value: "", label: "全部来源" },
    { value: "system", label: "系统结算" },
    { value: "exchange", label: "交易中心" },
];

const sourceLabel = (s: string) =>
    sourceOptions.find((o) => o.value === s)?.label || s;

// items 为 JSON 字符串列，解析出科目明细
const itemsOf = (row: any) => {
    try {
        const items = JSON.parse(row.items || "{}");
        return Object.entries(items).map(([fee, amount]) => ({ fee, amount }));
    } catch (e) {
        return [];
    }
};

const load = async () => {
    loading.value = true;
    try {
        const params: any = {
            pageNo: pageQuery.pageNo,
            pageSize: pageQuery.pageSize,
            period: pageQuery.period,
        };
        if (pageQuery.source) params.source = pageQuery.source;
        const res: any = await $api.get(`/settlement/records`, { params });
        if (res.code === 0) {
            list.value = res.data.list || [];
            total.value = Number(res.data.total || 0);
        }
    } finally {
        loading.value = false;
    }
};

const onPageChange = (p: number) => {
    pageQuery.pageNo = p;
    load();
};

const search = () => {
    pageQuery.pageNo = 1;
    load();
};

// ---- 结算核对 ----
const reconcileVisible = ref(false);
const reconcileResult = ref<any>(null);
const reconciling = ref(false);

const doReconcile = async (row: any) => {
    reconciling.value = true;
    try {
        const res: any = await $api.post(`/settlement/records/${row.id}/reconcile`);
        if (res.code === 0) {
            reconcileResult.value = res.data;
            reconcileVisible.value = true;
            load();
        }
    } finally {
        reconciling.value = false;
    }
};

// ---- 差异工单 ----
const tickets = ref<any[]>([]);
const ticketTotal = ref(0);
const ticketQuery = reactive({ pageNo: 1, pageSize: 10, status: "" });

const ticketStatusOptions = [
    { value: "", label: "全部状态" },
    { value: "pending", label: "待处理" },
    { value: "processing", label: "处理中" },
    { value: "reviewed", label: "已复核" },
    { value: "closed", label: "已关闭" },
];

const ticketStatusLabel = (s: string) =>
    ticketStatusOptions.find((o) => o.value === s)?.label || s;

// history 为 JSON 字符串列，解析出留痕时间线
const historyOf = (row: any) => {
    try {
        const history = JSON.parse(row.history || "[]");
        return Array.isArray(history) ? history : [];
    } catch (e) {
        return [];
    }
};

const loadTickets = async () => {
    loading.value = true;
    try {
        const params: any = {
            pageNo: ticketQuery.pageNo,
            pageSize: ticketQuery.pageSize,
        };
        if (ticketQuery.status) params.status = ticketQuery.status;
        const res: any = await $api.get(`/settlement/tickets`, { params });
        if (res.code === 0) {
            tickets.value = res.data.list || [];
            ticketTotal.value = Number(res.data.total || 0);
        }
    } finally {
        loading.value = false;
    }
};

const onTicketPageChange = (p: number) => {
    ticketQuery.pageNo = p;
    loadTickets();
};

const searchTickets = () => {
    ticketQuery.pageNo = 1;
    loadTickets();
};

const processTicket = async (row: any, action: string) => {
    const isAssign = action === "assign";
    let body: any = { action, comment: "" };
    if (isAssign) {
        try {
            const { value } = await ElMessageBox.prompt("请输入处理人账号", "指派处理人", {
                confirmButtonText: "指派",
                cancelButtonText: "取消",
            });
            body.handler = value;
        } catch (e) {
            return;
        }
    }
    try {
        const { value } = await ElMessageBox.prompt(
            isAssign ? "备注" : "处理说明",
            actionLabel(action),
            { confirmButtonText: "确定", cancelButtonText: "取消", inputType: "textarea" }
        );
        body.comment = value || "";
    } catch (e) {
        return;
    }
    const res: any = await $api.post(`/settlement/tickets/${row.id}/process`, body);
    if (res.code === 0) {
        ElMessage.success("操作成功");
        loadTickets();
    }
};

const actionLabel = (action: string) => {
    const map: any = { assign: "指派处理人", process: "处理工单", review: "复核工单", close: "关闭工单" };
    return map[action] || action;
};

const actionDisabled = (row: any, action: string) => {
    const s = row.status;
    if (action === "assign" || action === "process") return s !== "pending";
    if (action === "review") return s !== "processing";
    if (action === "close") return s !== "reviewed";
    return false;
};

// ---- 结算单 OCR 识别 ----
const uploadRef = ref();
const ocrTaskId = ref("");
const ocrResult = ref<any>(null);
const ocrVisible = ref(false);
const uploading = ref(false);

const onFileChange = async (file: any) => {
    if (!file.raw) return;
    uploading.value = true;
    try {
        const formData = new FormData();
        formData.append("file", file.raw);
        const res: any = await $api.post(`/ocr/tasks`, formData, {
            headers: { "Content-Type": "multipart/form-data" },
        });
        if (res.code === 0) {
            ocrTaskId.value = res.data.taskId;
            await loadOcrResult();
        }
    } finally {
        uploading.value = false;
        if (uploadRef.value) uploadRef.value.clearFiles();
    }
};

const loadOcrResult = async () => {
    if (!ocrTaskId.value) return;
    const res: any = await $api.get(`/ocr/tasks/${ocrTaskId.value}`);
    if (res.code === 0) {
        ocrResult.value = res.data;
        ocrVisible.value = true;
    }
};

const ocrFields = (fields: any) => {
    if (!fields || typeof fields !== "object") return [];
    return Object.entries(fields)
        .filter(([k]) => !["confidence"].includes(k))
        .map(([fee, amount]) => ({ fee, amount }));
};

onMounted(() => {
    load();
    loadTickets();
});
</script>

<template>
    <div class="settlement-page">
        <el-tabs v-model="activeTab" class="settlement-tabs">
            <!-- 结算记录与核对 -->
            <el-tab-pane label="结算记录" name="records">
                <div class="toolbar">
                    <el-input
                        v-model="pageQuery.period"
                        placeholder="结算周期（如 2026-08）"
                        class="period-input"
                        clearable
                    />
                    <el-select v-model="pageQuery.source" class="source-select">
                        <el-option
                            v-for="o in sourceOptions"
                            :key="o.value"
                            :label="o.label"
                            :value="o.value"
                        />
                    </el-select>
                    <el-button type="primary" @click="search">查询</el-button>
                </div>
                <el-table :data="list" v-loading="loading" border stripe>
                    <el-table-column prop="settlementPeriod" label="结算周期" width="110" />
                    <el-table-column label="来源" width="110">
                        <template #default="{ row }">{{ sourceLabel(row.source) }}</template>
                    </el-table-column>
                    <el-table-column label="费用结构" min-width="260">
                        <template #default="{ row }">
                            <el-tag
                                v-for="item in itemsOf(row)"
                                :key="item.fee"
                                size="small"
                                class="fee-tag"
                            >
                                {{ item.fee }}: {{ Number(item.amount).toLocaleString() }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="总金额（元）" width="140">
                        <template #default="{ row }">
                            {{ Number(row.totalAmount).toLocaleString() }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="syncStatus" label="同步状态" width="100" />
                    <el-table-column label="操作" width="120" fixed="right">
                        <template #default="{ row }">
                            <el-button
                                type="primary"
                                link
                                :loading="reconciling"
                                @click="doReconcile(row)"
                            >
                                发起核对
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <el-pagination
                    class="pager"
                    layout="total, prev, pager, next"
                    :total="total"
                    :page-size="pageQuery.pageSize"
                    :current-page="pageQuery.pageNo"
                    @current-change="onPageChange"
                />
            </el-tab-pane>

            <!-- 差异工单 -->
            <el-tab-pane label="差异工单" name="tickets">
                <div class="toolbar">
                    <el-select v-model="ticketQuery.status" class="source-select">
                        <el-option
                            v-for="o in ticketStatusOptions"
                            :key="o.value"
                            :label="o.label"
                            :value="o.value"
                        />
                    </el-select>
                    <el-button type="primary" @click="searchTickets">查询</el-button>
                </div>
                <el-table :data="tickets" v-loading="loading" border stripe>
                    <el-table-column label="工单号" width="180">
                        <template #default="{ row }">{{ row.id }}</template>
                    </el-table-column>
                    <el-table-column prop="diffType" label="差异类型" width="130" />
                    <el-table-column label="差异金额（元）" width="150">
                        <template #default="{ row }">
                            {{ Number(row.diffAmount).toLocaleString() }}
                        </template>
                    </el-table-column>
                    <el-table-column label="状态" width="100">
                        <template #default="{ row }">
                            <el-tag :type="row.status === 'closed' ? 'success' : 'warning'">
                                {{ ticketStatusLabel(row.status) }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="handler" label="处理人" width="110" />
                    <el-table-column label="处理留痕" min-width="220">
                        <template #default="{ row }">
                            <el-tooltip placement="top">
                                <template #content>
                                    <div v-for="(ev, i) in historyOf(row)" :key="i" class="history-line">
                                        [{{ ev.time }}] {{ ev.action }} · {{ ev.operator }}
                                        <span v-if="ev.comment">：{{ ev.comment }}</span>
                                    </div>
                                </template>
                                <el-tag size="small" type="info">
                                    {{ historyOf(row).length }} 条留痕
                                </el-tag>
                            </el-tooltip>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="230" fixed="right">
                        <template #default="{ row }">
                            <el-button
                                v-for="a in ['assign', 'process', 'review', 'close']"
                                :key="a"
                                type="primary"
                                link
                                :disabled="actionDisabled(row, a)"
                                @click="processTicket(row, a)"
                            >
                                {{ actionLabel(a) }}
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <el-pagination
                    class="pager"
                    layout="total, prev, pager, next"
                    :total="ticketTotal"
                    :page-size="ticketQuery.pageSize"
                    :current-page="ticketQuery.pageNo"
                    @current-change="onTicketPageChange"
                />
            </el-tab-pane>

            <!-- 结算单 OCR 识别 -->
            <el-tab-pane label="结算单识别" name="ocr">
                <el-card shadow="never" class="ocr-card">
                    <template #header>上传结算单图片，自动识别电量/电价/费用（低置信进入人工复核）</template>
                    <el-upload
                        ref="uploadRef"
                        :auto-upload="false"
                        :show-file-list="false"
                        accept="image/*"
                        :on-change="onFileChange"
                    >
                        <el-button type="primary" :loading="uploading">
                            选择结算单图片
                        </el-button>
                    </el-upload>
                </el-card>
            </el-tab-pane>
        </el-tabs>

        <!-- 核对结果弹窗 -->
        <el-dialog v-model="reconcileVisible" title="结算核对结果" width="640">
            <template v-if="reconcileResult">
                <el-descriptions :column="3" border>
                    <el-descriptions-item label="核对状态">
                        <el-tag :type="reconcileResult.status === 'consistent' ? 'success' : 'danger'">
                            {{ reconcileResult.status === 'consistent' ? '一致' : '存在差异' }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="通过率">
                        {{ (Number(reconcileResult.passRate) * 100).toFixed(2) }}%
                    </el-descriptions-item>
                    <el-descriptions-item label="差异金额">
                        {{ Number(reconcileResult.diffAmount).toLocaleString() }} 元
                    </el-descriptions-item>
                </el-descriptions>
                <el-table
                    :data="reconcileResult.checkItems || []"
                    border
                    size="small"
                    class="check-table"
                >
                    <el-table-column prop="fee" label="科目" />
                    <el-table-column label="系统（元）">
                        <template #default="{ row }">{{ Number(row.system).toLocaleString() }}</template>
                    </el-table-column>
                    <el-table-column label="交易中心（元）">
                        <template #default="{ row }">{{ Number(row.exchange).toLocaleString() }}</template>
                    </el-table-column>
                    <el-table-column label="差异（元）">
                        <template #default="{ row }">{{ Number(row.diff).toLocaleString() }}</template>
                    </el-table-column>
                    <el-table-column label="结论" width="90">
                        <template #default="{ row }">
                            <el-tag :type="row.consistent ? 'success' : 'danger'" size="small">
                                {{ row.consistent ? '一致' : '差异' }}
                            </el-tag>
                        </template>
                    </el-table-column>
                </el-table>
            </template>
        </el-dialog>

        <!-- OCR 识别结果弹窗 -->
        <el-dialog v-model="ocrVisible" title="识别结果" width="520">
            <template v-if="ocrResult">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="识别状态">
                        <el-tag
                            :type="ocrResult.status === 'success' ? 'success' : 'warning'"
                        >
                            {{ ocrResult.status }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="置信度">
                        {{ (Number(ocrResult.confidence) * 100).toFixed(2) }}%
                    </el-descriptions-item>
                    <el-descriptions-item label="人工复核">
                        {{ ocrResult.reviewStatus }}
                    </el-descriptions-item>
                </el-descriptions>
                <el-table :data="ocrFields(ocrResult.fields)" border size="small" class="check-table">
                    <el-table-column prop="fee" label="字段" />
                    <el-table-column label="数值">
                        <template #default="{ row }">
                            {{ typeof row.amount === 'number' ? Number(row.amount).toLocaleString() : row.amount }}
                        </template>
                    </el-table-column>
                </el-table>
            </template>
        </el-dialog>
    </div>
</template>

<style lang="scss" scoped>
.settlement-page {
    padding: 16px;
    .toolbar {
        display: flex;
        gap: 8px;
        margin-bottom: 12px;
        .period-input {
            width: 200px;
        }
        .source-select {
            width: 150px;
        }
    }
    .pager {
        margin-top: 12px;
        justify-content: flex-end;
    }
    .fee-tag {
        margin-right: 6px;
    }
    .history-line {
        line-height: 1.8;
    }
    .ocr-card {
        max-width: 720px;
    }
    .check-table {
        margin-top: 12px;
    }
}
</style>
