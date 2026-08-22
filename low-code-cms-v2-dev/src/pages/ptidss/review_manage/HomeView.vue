<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import $api from "@/api/Axios";

// PTIDSS 复盘考核（对齐 OpenAPI V1.0 /review/** 与 /assessment/**：复盘报告/策略回流/考核指标/考核结果/申诉）

const activeTab = ref("reports");
const loading = ref(false);

// ---- 复盘报告（契约无列表接口，本地会话记录，对齐 decision 模式） ----
const reportForm = reactive({
    reportType: "weekly",
    startDate: "",
    endDate: "",
    focusTopics: "",
});

const reportTypeOptions = [
    { value: "weekly", label: "周报" },
    { value: "monthly", label: "月报" },
    { value: "special", label: "专项" },
];

const LOCAL_KEY = "ptidss_review_reports";
const localReports = ref<any[]>(
    JSON.parse(localStorage.getItem(LOCAL_KEY) || "[]")
);

const generate = async () => {
    if (!reportForm.startDate || !reportForm.endDate) {
        ElMessage.warning("请选择复盘周期");
        return;
    }
    loading.value = true;
    try {
        const body: any = {
            reportType: reportForm.reportType,
            startDate: reportForm.startDate,
            endDate: reportForm.endDate,
        };
        if (reportForm.focusTopics) {
            body.focusTopics = reportForm.focusTopics
                .split(/[,，]/)
                .map((s: string) => s.trim())
                .filter(Boolean);
        }
        const res: any = await $api.post(`/review/reports`, body);
        if (res.code === 0) {
            const record = {
                reportId: res.data.reportId,
                reportType: reportForm.reportType,
                startDate: reportForm.startDate,
                endDate: reportForm.endDate,
                createdAt: new Date().toLocaleString(),
            };
            localReports.value.unshift(record);
            localStorage.setItem(LOCAL_KEY, JSON.stringify(localReports.value));
            ElMessage.success("复盘报告已生成");
            openDetail(res.data.reportId);
        }
    } finally {
        loading.value = false;
    }
};

const reportTypeLabel = (t: string) =>
    reportTypeOptions.find((o) => o.value === t)?.label || t;

// ---- 报告详情 ----
const detailVisible = ref(false);
const detail = ref<any>(null);
const detailLoading = ref(false);

const openDetail = async (reportId: string) => {
    detailVisible.value = true;
    detailLoading.value = true;
    try {
        const res: any = await $api.get(`/review/reports/${reportId}`);
        if (res.code === 0) {
            detail.value = res.data;
        }
    } finally {
        detailLoading.value = false;
    }
};

const layerName = (layer: string) => {
    const map: any = { forecast: "预测偏差", decision: "决策偏差", execution: "执行偏差" };
    return map[layer] || layer;
};

// ---- 策略回流 ----
const feedbackForm = reactive({
    strategyCode: "",
    feedback: "effective",
    updatedParams: "",
});

const feedbackOptions = [
    { value: "effective", label: "有效" },
    { value: "invalid", label: "失效" },
    { value: "adjust", label: "调整" },
];

const strategyOptions = [
    { value: "STRAT-DA-PRICE", label: "日前分时段报价策略" },
    { value: "STRAT-MID-LONG", label: "中长期滚动建仓策略" },
    { value: "STRAT-SPOT-ARB", label: "现货峰谷套利策略" },
    { value: "STRAT-RISK-CTRL", label: "偏差风险控制策略" },
];

const strategyName = (code: string) =>
    strategyOptions.find((o) => o.value === code)?.label || code;

const submitFeedback = async () => {
    if (!feedbackForm.strategyCode) {
        ElMessage.warning("请选择策略");
        return;
    }
    const body: any = {
        strategyCode: feedbackForm.strategyCode,
        feedback: feedbackForm.feedback,
    };
    if (detail.value) body.reviewId = detail.value.id;
    if (feedbackForm.feedback === "adjust" && feedbackForm.updatedParams) {
        try {
            body.updatedParams = JSON.parse(feedbackForm.updatedParams);
        } catch (e) {
            ElMessage.warning("调整参数须为合法 JSON");
            return;
        }
    }
    const res: any = await $api.post(`/review/strategy-feedback`, body);
    if (res.code === 0) {
        ElMessage.success("策略回流已登记");
        feedbackForm.strategyCode = "";
        feedbackForm.updatedParams = "";
    }
};

// ---- 考核 ----
const indicators = ref<any[]>([]);
const results = ref<any[]>([]);
const assessQuery = reactive({ period: "2026-08", scope: "" });

const scopeOptions = [
    { value: "", label: "全部范围" },
    { value: "team", label: "团队" },
    { value: "personal", label: "个人" },
];

const loadIndicators = async () => {
    const res: any = await $api.get(`/assessment/indicators`);
    if (res.code === 0) indicators.value = res.data || [];
};

const loadResults = async () => {
    loading.value = true;
    try {
        const params: any = { period: assessQuery.period };
        if (assessQuery.scope) params.scope = assessQuery.scope;
        const res: any = await $api.get(`/assessment/results`, { params });
        if (res.code === 0) results.value = res.data || [];
    } finally {
        loading.value = false;
    }
};

// scores 为 JSON 字符串列，解析分项
const scoresOf = (row: any) => {
    try {
        const scores = JSON.parse(row.scores || "{}");
        return Object.entries(scores).map(([code, score]) => ({ code, score }));
    } catch (e) {
        return [];
    }
};

const resultStatusLabel = (s: string) => {
    const map: any = {
        pending: "待确认",
        confirmed: "已确认",
        appealing: "申诉中",
        corrected: "已重算",
    };
    return map[s] || s;
};

// ---- 申诉 ----
const appealVisible = ref(false);
const appealForm = reactive({
    resultId: "",
    appealReason: "",
    evidenceUrls: "",
});

const openAppeal = (row: any) => {
    appealForm.resultId = row.id;
    appealForm.appealReason = "";
    appealForm.evidenceUrls = "";
    appealVisible.value = true;
};

const submitAppeal = async () => {
    if (!appealForm.appealReason) {
        ElMessage.warning("请填写申诉理由");
        return;
    }
    const body: any = { resultId: appealForm.resultId, appealReason: appealForm.appealReason };
    if (appealForm.evidenceUrls) {
        body.evidenceUrls = appealForm.evidenceUrls
            .split(/[,，]/)
            .map((s: string) => s.trim())
            .filter(Boolean);
    }
    const res: any = await $api.post(`/assessment/appeals`, body);
    if (res.code === 0) {
        ElMessage.success("申诉已提交");
        appealVisible.value = false;
        loadResults();
    }
};

const processAppeal = async (row: any, decision: string) => {
    try {
        const { value } = await ElMessageBox.prompt(
            decision === "approved" ? "批准意见" : "驳回理由",
            decision === "approved" ? "批准申诉" : "驳回申诉",
            { confirmButtonText: "确定", cancelButtonText: "取消", inputType: "textarea" }
        );
        const res: any = await $api.post(`/assessment/appeals/${row.id}/process`, {
            decision,
            comment: value || "",
        });
        if (res.code === 0) {
            ElMessage.success("审核完成");
            loadResults();
        }
    } catch (e) {
        // 取消输入
    }
};

onMounted(() => {
    loadIndicators();
    loadResults();
});
</script>

<template>
    <div class="review-page">
        <el-tabs v-model="activeTab" class="review-tabs">
            <!-- 复盘报告 -->
            <el-tab-pane label="智能复盘" name="reports">
                <div class="report-layout">
                    <el-card shadow="never" class="generate-card">
                        <template #header>生成复盘报告（FR-RS-01：决策-结果-原因-改进闭环）</template>
                        <el-form label-width="90px">
                            <el-form-item label="报告类型">
                                <el-radio-group v-model="reportForm.reportType">
                                    <el-radio v-for="o in reportTypeOptions" :key="o.value" :value="o.value">
                                        {{ o.label }}
                                    </el-radio>
                                </el-radio-group>
                            </el-form-item>
                            <el-form-item label="复盘周期">
                                <el-date-picker
                                    v-model="reportForm.startDate"
                                    type="date"
                                    value-format="YYYY-MM-DD"
                                    placeholder="开始日期"
                                    class="date-input"
                                />
                                <span class="date-sep">至</span>
                                <el-date-picker
                                    v-model="reportForm.endDate"
                                    type="date"
                                    value-format="YYYY-MM-DD"
                                    placeholder="结束日期"
                                    class="date-input"
                                />
                            </el-form-item>
                            <el-form-item label="专项主题">
                                <el-input
                                    v-model="reportForm.focusTopics"
                                    placeholder="逗号分隔（可选）"
                                />
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" :loading="loading" @click="generate">
                                    生成复盘报告
                                </el-button>
                            </el-form-item>
                        </el-form>
                    </el-card>

                    <el-card shadow="never" class="list-card">
                        <template #header>已生成报告（本地会话记录）</template>
                        <el-empty v-if="!localReports.length" description="暂无报告，请先生成" />
                        <el-table v-else :data="localReports" border>
                            <el-table-column label="报告" min-width="160">
                                <template #default="{ row }">
                                    {{ reportTypeLabel(row.reportType) }}（{{ row.reportId }}）
                                </template>
                            </el-table-column>
                            <el-table-column prop="startDate" label="开始" width="110" />
                            <el-table-column prop="endDate" label="结束" width="110" />
                            <el-table-column prop="createdAt" label="生成时间" width="170" />
                            <el-table-column label="操作" width="90">
                                <template #default="{ row }">
                                    <el-button type="primary" link @click="openDetail(row.reportId)">
                                        查看
                                    </el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </el-card>
                </div>

                <!-- 策略回流 -->
                <el-card shadow="never" class="feedback-card">
                    <template #header>策略回流（复盘结论沉淀策略库，反哺决策引擎）</template>
                    <el-form inline>
                        <el-form-item label="策略">
                            <el-select v-model="feedbackForm.strategyCode" class="strategy-select">
                                <el-option
                                    v-for="o in strategyOptions"
                                    :key="o.value"
                                    :label="o.label"
                                    :value="o.value"
                                />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="结论">
                            <el-radio-group v-model="feedbackForm.feedback">
                                <el-radio v-for="o in feedbackOptions" :key="o.value" :value="o.value">
                                    {{ o.label }}
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item v-if="feedbackForm.feedback === 'adjust'" label="调整参数">
                            <el-input
                                v-model="feedbackForm.updatedParams"
                                placeholder='如 {"priceBias": 0.02}'
                                class="params-input"
                            />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="submitFeedback">登记回流</el-button>
                        </el-form-item>
                    </el-form>
                </el-card>
            </el-tab-pane>

            <!-- 复盘报告详情弹窗 -->
            <el-dialog v-model="detailVisible" title="复盘报告详情" width="760" :close-on-click-modal="false">
                <div v-loading="detailLoading">
                    <template v-if="detail">
                        <el-descriptions :column="3" border size="small">
                            <el-descriptions-item label="类型">
                                {{ reportTypeLabel(detail.reportType) }}
                            </el-descriptions-item>
                            <el-descriptions-item label="周期">
                                {{ detail.periodStart }} ~ {{ detail.periodEnd }}
                            </el-descriptions-item>
                            <el-descriptions-item label="状态">{{ detail.status }}</el-descriptions-item>
                        </el-descriptions>

                        <h4 class="section-title">摘要</h4>
                        <el-descriptions
                            v-if="detail.summary"
                            :column="3"
                            border
                            size="small"
                        >
                            <el-descriptions-item label="收益（元）">
                                {{ Number(detail.summary.revenue).toLocaleString() }}
                            </el-descriptions-item>
                            <el-descriptions-item label="成交电量（MWh）">
                                {{ Number(detail.summary.tradeVolume).toLocaleString() }}
                            </el-descriptions-item>
                            <el-descriptions-item label="预测偏差">
                                {{ detail.summary.forecastError }}%
                            </el-descriptions-item>
                        </el-descriptions>

                        <h4 class="section-title">三层偏差归因（预测/决策/执行）</h4>
                        <el-card
                            v-for="layer in detail.deviationAnalysis?.layers || []"
                            :key="layer.layer"
                            shadow="never"
                            class="layer-card"
                        >
                            <template #header>{{ layerName(layer.layer) }}</template>
                            <el-table :data="layer.items" border size="small">
                                <el-table-column prop="item" label="归因项" />
                                <el-table-column prop="value" label="偏差值" width="90" />
                                <el-table-column label="收益影响（元）" width="130">
                                    <template #default="{ row }">
                                        {{ Number(row.impactAmount).toLocaleString() }}
                                    </template>
                                </el-table-column>
                                <el-table-column prop="reason" label="原因" />
                                <el-table-column label="方向" width="90">
                                    <template #default="{ row }">
                                        <el-tag :type="row.direction === 'positive' ? 'success' : 'danger'" size="small">
                                            {{ row.direction === 'positive' ? '正向' : '负向' }}
                                        </el-tag>
                                    </template>
                                </el-table-column>
                            </el-table>
                        </el-card>

                        <h4 class="section-title">策略评估</h4>
                        <el-table
                            v-if="detail.strategyEval"
                            :data="detail.strategyEval.strategies || []"
                            border
                            size="small"
                        >
                            <el-table-column label="策略" min-width="180">
                                <template #default="{ row }">
                                    {{ row.strategyCode }}（{{ row.name }}）
                                </template>
                            </el-table-column>
                            <el-table-column prop="score" label="评分" width="80" />
                            <el-table-column prop="evaluation" label="评估结论" />
                        </el-table>
                        <div v-if="detail.strategyEval" class="best-strategy">
                            最佳策略：{{ strategyName(detail.strategyEval.bestStrategy) }}
                        </div>

                        <h4 class="section-title">改进建议</h4>
                        <ul v-if="detail.suggestions?.length" class="suggestion-list">
                            <li v-for="(s, i) in detail.suggestions" :key="i">{{ s }}</li>
                        </ul>
                    </template>
                </div>
            </el-dialog>

            <!-- 考核 -->
            <el-tab-pane label="交易考核" name="assessment">
                <el-card shadow="never" class="indicator-card">
                    <template #header>考核指标体系（FR-DM-07）</template>
                    <el-table :data="indicators" border size="small">
                        <el-table-column prop="code" label="编码" width="110" />
                        <el-table-column prop="name" label="指标" min-width="140" />
                        <el-table-column label="权重" width="90">
                            <template #default="{ row }">
                                {{ (Number(row.weight) * 100).toFixed(0) }}%
                            </template>
                        </el-table-column>
                        <el-table-column label="评分规则" min-width="180">
                            <template #default="{ row }">
                                {{ JSON.stringify(row.scoringRule || {}) }}
                            </template>
                        </el-table-column>
                        <el-table-column prop="dataSource" label="数据来源" width="150" />
                    </el-table>
                </el-card>

                <el-card shadow="never" class="result-card">
                    <template #header>
                        <div class="result-header">
                            考核结果
                            <div class="result-tools">
                                <el-input
                                    v-model="assessQuery.period"
                                    placeholder="周期（如 2026-08）"
                                    class="period-input"
                                />
                                <el-select v-model="assessQuery.scope" class="scope-select">
                                    <el-option
                                        v-for="o in scopeOptions"
                                        :key="o.value"
                                        :label="o.label"
                                        :value="o.value"
                                    />
                                </el-select>
                                <el-button type="primary" @click="loadResults">查询</el-button>
                            </div>
                        </div>
                    </template>
                    <el-table :data="results" v-loading="loading" border>
                        <el-table-column label="结果号" width="170">
                            <template #default="{ row }">{{ row.id }}</template>
                        </el-table-column>
                        <el-table-column prop="scope" label="范围" width="90" />
                        <el-table-column prop="period" label="周期" width="100" />
                        <el-table-column label="总分" width="90">
                            <template #default="{ row }">{{ row.totalScore }}</template>
                        </el-table-column>
                        <el-table-column label="分项" min-width="220">
                            <template #default="{ row }">
                                <el-tag
                                    v-for="s in scoresOf(row)"
                                    :key="s.code"
                                    size="small"
                                    class="score-tag"
                                >
                                    {{ s.code }}: {{ s.score }}
                                </el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column label="状态" width="100">
                            <template #default="{ row }">
                                <el-tag :type="row.status === 'corrected' ? 'warning' : 'success'" size="small">
                                    {{ resultStatusLabel(row.status) }}
                                </el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="140" fixed="right">
                            <template #default="{ row }">
                                <el-button type="primary" link @click="openAppeal(row)">申诉</el-button>
                                <el-button
                                    type="warning"
                                    link
                                    :disabled="row.status !== 'appealing'"
                                    @click="processAppeal(row, 'approved')"
                                >
                                    批准
                                </el-button>
                                <el-button
                                    type="danger"
                                    link
                                    :disabled="row.status !== 'appealing'"
                                    @click="processAppeal(row, 'rejected')"
                                >
                                    驳回
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-tab-pane>
        </el-tabs>

        <!-- 申诉弹窗 -->
        <el-dialog v-model="appealVisible" title="提交考核申诉" width="520">
            <el-form label-width="90px">
                <el-form-item label="结果号">{{ appealForm.resultId }}</el-form-item>
                <el-form-item label="申诉理由">
                    <el-input
                        v-model="appealForm.appealReason"
                        type="textarea"
                        :rows="3"
                        placeholder="请说明申诉原因"
                    />
                </el-form-item>
                <el-form-item label="证据材料">
                    <el-input
                        v-model="appealForm.evidenceUrls"
                        placeholder="URL 逗号分隔（可选）"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="appealVisible = false">取消</el-button>
                <el-button type="primary" @click="submitAppeal">提交</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<style lang="scss" scoped>
.review-page {
    padding: 16px;
    .report-layout {
        display: flex;
        gap: 16px;
        .generate-card {
            width: 460px;
            flex-shrink: 0;
        }
        .list-card {
            flex: 1;
        }
    }
    .date-input {
        width: 140px;
    }
    .date-sep {
        margin: 0 8px;
        color: #999;
    }
    .feedback-card {
        margin-top: 16px;
        .strategy-select {
            width: 200px;
        }
        .params-input {
            width: 220px;
        }
    }
    .section-title {
        margin: 14px 0 8px;
        font-size: 14px;
    }
    .layer-card {
        margin-bottom: 8px;
    }
    .best-strategy {
        margin-top: 8px;
        color: #409eff;
        font-size: 13px;
    }
    .suggestion-list {
        margin: 0;
        padding-left: 18px;
        line-height: 1.9;
    }
    .result-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        .result-tools {
            display: flex;
            gap: 8px;
            .period-input {
                width: 150px;
            }
            .scope-select {
                width: 120px;
            }
        }
    }
    .indicator-card {
        margin-bottom: 16px;
    }
    .score-tag {
        margin-right: 6px;
    }
}
</style>
