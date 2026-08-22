<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import $api from "@/api/Axios";

// PTIDSS 辅助决策（对齐 OpenAPI V1.0 /decision/**：会话编排/人机确认/修改依据/依据链回溯）
// 契约无会话列表接口：前端以本地会话记录（sessionStorage）管理已创建会话，详情实时拉取

const loading = ref(false);

const sessionTypeOptions = [
    { value: "rolling", label: "日滚动优化" },
    { value: "spot_quote", label: "现货报价" },
    { value: "joint_optimize", label: "联合优化" },
];

const sessionTypeLabel = (t: string) =>
    sessionTypeOptions.find((o) => o.value === t)?.label || t;

const humanStatusMap: Record<string, string> = {
    pending: "待人工审核",
    confirmed: "已确认",
    modified: "已修改",
    rejected: "已驳回",
};

// ---- 发起会话 ----
const form = reactive({
    sessionType: "rolling",
    tradeDate: "",
    scenario: "",
    agents: [] as string[],
});

const agentOptions = [
    { value: "predict", label: "负荷预测 Agent" },
    { value: "quote", label: "报价优化 Agent" },
    { value: "risk", label: "风险评估 Agent" },
    { value: "settlement", label: "结算测算 Agent" },
    { value: "compliance", label: "合规校验 Agent" },
    { value: "review", label: "总控审查 Agent" },
];

const createResp = ref<any>(null);

const submitCreate = async () => {
    if (!form.tradeDate) {
        ElMessage.warning("请选择交易日期");
        return;
    }
    loading.value = true;
    try {
        const payload: any = {
            sessionType: form.sessionType,
            tradeDate: form.tradeDate,
        };
        if (form.scenario) payload.scenario = form.scenario;
        if (form.agents.length) payload.agents = form.agents;
        const res: any = await $api.post(`/decision/sessions`, payload);
        if (res.code === 0) {
            createResp.value = res.data;
            ElMessage.success("决策会话已发起，编排执行完成");
            // 本地会话记录（契约无列表接口）
            const rec = {
                sessionId: res.data.sessionId,
                sessionType: form.sessionType,
                tradeDate: form.tradeDate,
                status: res.data.status,
                createdAt: new Date().toLocaleString(),
            };
            sessions.value.unshift(rec);
            localStorage.setItem("ptidss_decision_sessions", JSON.stringify(sessions.value));
            detail.value = null;
            await loadDetail(res.data.sessionId);
        }
    } finally {
        loading.value = false;
    }
};

// ---- 本地会话记录 ----
interface SessionRec {
    sessionId: string;
    sessionType: string;
    tradeDate: string;
    status: string;
    createdAt: string;
}

const sessions = ref<SessionRec[]>([]);

// ---- 会话详情 ----
const detail = ref<any>(null);
const detailLoading = ref(false);

const loadDetail = async (sessionId: string) => {
    detailLoading.value = true;
    try {
        const res: any = await $api.get(`/decision/sessions/${sessionId}`);
        if (res.code === 0) {
            detail.value = res.data;
        }
    } finally {
        detailLoading.value = false;
    }
};

const onSelectSession = (rec: SessionRec) => {
    loadDetail(rec.sessionId);
};

// ---- 确认策略 ----
const confirmSession = () => {
    ElMessageBox.confirm(
        "确认采用该最终策略并写入申报准备吗？确认后进入已确认状态。",
        "确认策略",
        { type: "warning" }
    ).then(async () => {
        const res: any = await $api.post(`/decision/sessions/${detail.value.sessionId}/confirm`);
        if (res.code === 0) {
            ElMessage.success("策略已确认");
            loadDetail(detail.value.sessionId);
        }
    });
};

// ---- 修改策略 ----
const modifyVisible = ref(false);
const modifyForm = reactive({
    reason: "",
    secondReviewer: "",
    modification: { volume: 0 },
});

const openModify = () => {
    Object.assign(modifyForm, { reason: "", secondReviewer: "", modification: { volume: 0 } });
    modifyVisible.value = true;
};

const submitModify = async () => {
    if (!modifyForm.reason) {
        ElMessage.warning("修改依据必填（FR-DM-05）");
        return;
    }
    const res: any = await $api.post(`/decision/sessions/${detail.value.sessionId}/modify`, {
        modifications: [modifyForm.modification],
        reason: modifyForm.reason,
        secondReviewer: modifyForm.secondReviewer || undefined,
    });
    if (res.code === 0) {
        ElMessage.success("策略修改已提交（已记录修改依据）");
        modifyVisible.value = false;
        loadDetail(detail.value.sessionId);
    }
};

// ---- 依据链回溯 ----
const evidenceVisible = ref(false);
const evidence = ref<any>(null);
const evidenceLoading = ref(false);

const loadEvidence = async () => {
    evidenceVisible.value = true;
    evidenceLoading.value = true;
    try {
        const res: any = await $api.get(`/decision/sessions/${detail.value.sessionId}/evidence`);
        if (res.code === 0) {
            evidence.value = res.data;
        }
    } finally {
        evidenceLoading.value = false;
    }
};

const strategyText = (strategy: any) => JSON.stringify(strategy || {}, null, 2);
const evidenceText = (obj: any) => JSON.stringify(obj || [], null, 2);

const today = () => {
    const d = new Date();
    return (
        d.getFullYear() +
        "-" +
        String(d.getMonth() + 1).padStart(2, "0") +
        "-" +
        String(d.getDate()).padStart(2, "0")
    );
};

onMounted(() => {
    form.tradeDate = today();
    // 恢复本地会话记录
    try {
        const saved = localStorage.getItem("ptidss_decision_sessions");
        if (saved) sessions.value = JSON.parse(saved);
    } catch (e) {
        sessions.value = [];
    }
});
</script>

<template>
    <div class="page">
        <el-row :gutter="16">
            <el-col :span="8">
                <el-card shadow="never">
                    <template #header>
                        <span class="card-title">发起决策会话</span>
                    </template>
                    <el-form label-width="80px">
                        <el-form-item label="会话类型" required>
                            <el-select v-model="form.sessionType" style="width: 100%">
                                <el-option v-for="o in sessionTypeOptions" :key="o.value" :value="o.value" :label="o.label" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="交易日期" required>
                            <el-date-picker
                                v-model="form.tradeDate"
                                type="date"
                                value-format="YYYY-MM-DD"
                                placeholder="选择交易日期"
                                style="width: 100%"
                            />
                        </el-form-item>
                        <el-form-item label="场景">
                            <el-input v-model="form.scenario" placeholder="可选，如：高温保供" />
                        </el-form-item>
                        <el-form-item label="参与智能体">
                            <el-checkbox-group v-model="form.agents">
                                <el-checkbox v-for="a in agentOptions" :key="a.value" :value="a.value" class="agent-check">
                                    {{ a.label }}
                                </el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" :loading="loading" style="width: 100%" @click="submitCreate">
                                发起编排
                            </el-button>
                        </el-form-item>
                    </el-form>

                    <el-alert
                        v-if="createResp"
                        type="success"
                        :closable="false"
                        show-icon
                        title="会话创建成功"
                    >
                        <div class="resp-line">会话 ID：{{ createResp.sessionId }}</div>
                        <div class="resp-line">编排状态：{{ createResp.status }}</div>
                    </el-alert>

                    <el-divider />

                    <div class="card-title">本地会话记录</div>
                    <div v-if="!sessions.length" class="empty-tip">暂无会话，发起后将在此记录</div>
                    <div
                        v-for="rec in sessions"
                        :key="rec.sessionId"
                        class="session-item"
                        :class="{ active: detail && detail.sessionId === rec.sessionId }"
                        @click="onSelectSession(rec)"
                    >
                        <div class="session-line1">
                            <span class="session-type">{{ sessionTypeLabel(rec.sessionType) }}</span>
                            <span class="session-date">{{ rec.tradeDate }}</span>
                        </div>
                        <div class="session-line2">
                            {{ rec.sessionId }} · {{ rec.createdAt }}
                        </div>
                    </div>
                </el-card>
            </el-col>

            <el-col :span="16">
                <el-card shadow="never" v-loading="detailLoading">
                    <template #header>
                        <div class="detail-head">
                            <span class="card-title">会话详情</span>
                            <div v-if="detail" class="detail-actions">
                                <el-button
                                    v-if="detail.humanReviewStatus === 'pending'"
                                    type="success"
                                    size="small"
                                    @click="confirmSession"
                                >
                                    确认策略
                                </el-button>
                                <el-button
                                    v-if="detail.humanReviewStatus === 'pending'"
                                    type="warning"
                                    size="small"
                                    @click="openModify"
                                >
                                    修改策略
                                </el-button>
                                <el-button v-if="detail" size="small" @click="loadEvidence">
                                    依据链回溯
                                </el-button>
                            </div>
                        </div>
                    </template>

                    <el-empty v-if="!detail" description="请选择左侧会话查看详情，或发起新会话" />

                    <template v-else>
                        <el-descriptions :column="3" border class="desc">
                            <el-descriptions-item label="会话编号">{{ detail.sessionNo }}</el-descriptions-item>
                            <el-descriptions-item label="类型">
                                {{ sessionTypeLabel(detail.sessionType) }}
                            </el-descriptions-item>
                            <el-descriptions-item label="交易日期">
                                {{ String(detail.tradeDate || "").substring(0, 10) }}
                            </el-descriptions-item>
                            <el-descriptions-item label="编排状态">{{ detail.status }}</el-descriptions-item>
                            <el-descriptions-item label="人工审核">
                                <el-tag
                                    :type="detail.humanReviewStatus === 'confirmed' ? 'success' : detail.humanReviewStatus === 'modified' ? 'warning' : 'info'"
                                    size="small"
                                >
                                    {{ humanStatusMap[detail.humanReviewStatus] || detail.humanReviewStatus }}
                                </el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item label="审核人">{{ detail.reviewedBy || "-" }}</el-descriptions-item>
                            <el-descriptions-item label="参与智能体" :span="3">
                                <el-tag
                                    v-for="a in detail.agents || []"
                                    :key="a"
                                    size="small"
                                    style="margin-right: 6px"
                                >
                                    {{ a }}
                                </el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item v-if="detail.modifyReason" label="修改依据" :span="3">
                                {{ detail.modifyReason }}
                            </el-descriptions-item>
                            <el-descriptions-item v-if="detail.reviewer2" label="复核人" :span="3">
                                {{ detail.reviewer2 }}
                            </el-descriptions-item>
                        </el-descriptions>

                        <div class="section">
                            <div class="section-title">最终策略</div>
                            <pre class="json-box">{{ strategyText(detail.finalStrategy) }}</pre>
                        </div>

                        <div class="section">
                            <div class="section-title">依据摘要（Agent 输出 / 置信度）</div>
                            <pre class="json-box">{{ strategyText(detail.evidenceSummary) }}</pre>
                        </div>
                    </template>
                </el-card>
            </el-col>
        </el-row>

        <!-- 修改策略弹窗 -->
        <el-dialog v-model="modifyVisible" title="修改策略（需记录修改依据）" width="560px">
            <el-form label-width="110px">
                <el-form-item label="修改量（MWh）">
                    <el-input-number v-model="modifyForm.modification.volume" :min="-5000" :max="5000" />
                    <div class="tip">修改量超过 15% 时系统要求填写复核人（双人复核）</div>
                </el-form-item>
                <el-form-item label="修改依据" required>
                    <el-input
                        v-model="modifyForm.reason"
                        type="textarea"
                        :rows="3"
                        placeholder="必填（FR-DM-05）：说明市场变化/负荷偏差等修改理由"
                    />
                </el-form-item>
                <el-form-item label="复核人">
                    <el-input v-model="modifyForm.secondReviewer" placeholder="修改量较大时必填" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="modifyVisible = false">取消</el-button>
                <el-button type="primary" @click="submitModify">提交修改</el-button>
            </template>
        </el-dialog>

        <!-- 依据链回溯弹窗 -->
        <el-dialog v-model="evidenceVisible" title="依据链回溯（Agent 输入/输出/冲突仲裁）" width="760px">
            <div v-loading="evidenceLoading">
                <el-descriptions v-if="evidence" :column="2" border class="desc">
                    <el-descriptions-item label="会话编号">{{ evidence.sessionNo }}</el-descriptions-item>
                    <el-descriptions-item label="编排器版本">{{ evidence.orchestratorVersion }}</el-descriptions-item>
                    <el-descriptions-item label="人工审核状态" :span="2">
                        {{ humanStatusMap[evidence.humanReviewStatus] || evidence.humanReviewStatus }}
                    </el-descriptions-item>
                </el-descriptions>
                <div v-if="evidence" class="section">
                    <div class="section-title">Agent 执行链</div>
                    <pre class="json-box json-evidence">{{ evidenceText(evidence.agents) }}</pre>
                </div>
                <div v-if="evidence && evidence.conflicts?.length" class="section">
                    <div class="section-title">冲突仲裁记录（报价/风险价差 > 60 元/MWh）</div>
                    <pre class="json-box json-evidence">{{ evidenceText(evidence.conflicts) }}</pre>
                </div>
                <el-empty v-else-if="!evidenceLoading" description="无冲突记录" />
            </div>
        </el-dialog>
    </div>
</template>

<style scoped lang="scss">
.page {
    padding: 16px;

    .card-title {
        font-weight: 600;
        color: #1f3b6b;
    }

    .agent-check {
        width: 100%;
        margin-right: 0;
    }

    .resp-line {
        font-size: 12px;
        line-height: 20px;
    }

    .empty-tip {
        color: #c0c4cc;
        font-size: 13px;
        padding: 12px 0;
        text-align: center;
    }

    .session-item {
        padding: 8px 10px;
        border: 1px solid #ebeef5;
        border-radius: 4px;
        margin-bottom: 8px;
        cursor: pointer;
        transition: all 0.2s;

        &:hover {
            border-color: #409eff;
        }

        &.active {
            border-color: #409eff;
            background: #ecf5ff;
        }

        .session-line1 {
            display: flex;
            justify-content: space-between;
            font-size: 13px;

            .session-type {
                font-weight: 600;
                color: #303133;
            }

            .session-date {
                color: #838c99;
            }
        }

        .session-line2 {
            font-size: 12px;
            color: #a8abb2;
            margin-top: 4px;
        }
    }

    .detail-head {
        display: flex;
        align-items: center;
        justify-content: space-between;
    }

    .desc {
        margin-bottom: 12px;
    }

    .section {
        margin-top: 12px;

        .section-title {
            font-weight: 600;
            color: #1f3b6b;
            margin-bottom: 8px;
        }

        .json-box {
            background: #f5f7fa;
            border: 1px solid #ebeef5;
            border-radius: 4px;
            padding: 10px 12px;
            font-size: 12px;
            max-height: 300px;
            overflow: auto;
            margin: 0;
            white-space: pre-wrap;
            word-break: break-all;
        }

        .json-evidence {
            max-height: 360px;
        }
    }

    .tip {
        font-size: 12px;
        color: #a8abb2;
        line-height: 18px;
        margin-top: 4px;
    }
}
</style>
