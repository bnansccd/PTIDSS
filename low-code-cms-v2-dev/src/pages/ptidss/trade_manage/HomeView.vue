<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import * as echarts from "echarts";
import $api from "@/api/Axios";

// PTIDSS 交易申报（对齐 OpenAPI V1.0 /trade/**：申报单/合规预检/提交/成交结果/持仓曲线）

const activeTab = ref("declaration");
const loading = ref(false);

// ---- 申报单列表 ----
const list = ref<any[]>([]);
const total = ref(0);
const pageQuery = reactive({ pageNo: 1, pageSize: 10, tradeDate: "", status: "" });

const statusOptions = [
    { value: "draft", label: "草稿" },
    { value: "pending_submit", label: "待提交" },
    { value: "submitted", label: "已提交" },
];

const marketTypeOptions = [
    { value: "intra_province", label: "省内市场" },
    { value: "inter_province", label: "省间市场" },
];

const stageOptions = [
    { value: "day_ahead", label: "日前" },
    { value: "real_time", label: "实时" },
    { value: "rolling", label: "滚动" },
];

const statusLabel = (s: string) =>
    statusOptions.find((o) => o.value === s)?.label || s;

const compliancePassed = (row: any) => {
    try {
        const check = JSON.parse(row.complianceCheck || "{}");
        return !!check.passed;
    } catch (e) {
        return false;
    }
};

// items 为 JSON 字符串列，需解析后取段数
const segmentsOf = (row: any) => {
    try {
        const items = JSON.parse(row.items || "[]");
        return Array.isArray(items) ? items.length : 0;
    } catch (e) {
        return 0;
    }
};

const load = async () => {
    loading.value = true;
    try {
        const params: any = {
            pageNo: pageQuery.pageNo,
            pageSize: pageQuery.pageSize,
        };
        if (pageQuery.tradeDate) params.tradeDate = pageQuery.tradeDate;
        if (pageQuery.status) params.status = pageQuery.status;
        const res: any = await $api.get(`/trade/declarations`, { params });
        if (res.code === 0) {
            list.value = res.data.list || [];
            total.value = res.data.total || 0;
        }
    } finally {
        loading.value = false;
    }
};

const onPageChange = (p: number) => {
    pageQuery.pageNo = p;
    load();
};

// ---- 合规明细弹窗 ----
const checkVisible = ref(false);
const checkDetail = ref<any>(null);
const viewCheck = (row: any) => {
    checkDetail.value = JSON.parse(row.complianceCheck || "{}");
    checkVisible.value = true;
};

// ---- 新建申报 ----
const dialogVisible = ref(false);
const formRef = ref();
const form = reactive({
    tradeDate: "",
    marketType: "intra_province",
    stage: "day_ahead",
    items: [] as any[],
});

const addItem = () => {
    if (form.items.length >= 10) {
        ElMessage.warning("申报段数不得超过 10 段");
        return;
    }
    form.items.push({ startTime: "", endTime: "", volume: 0, price: 0 });
};

const removeItem = (idx: number) => {
    form.items.splice(idx, 1);
};

const openCreate = () => {
    Object.assign(form, {
        tradeDate: "",
        marketType: "intra_province",
        stage: "day_ahead",
        items: [],
    });
    addItem();
    createResult.value = null;
    dialogVisible.value = true;
};

const createResult = ref<any>(null);

const submitCreate = async () => {
    if (!form.tradeDate) {
        ElMessage.warning("请选择交易日期");
        return;
    }
    if (!form.items.length) {
        ElMessage.warning("请至少添加一个申报段");
        return;
    }
    for (const it of form.items) {
        if (!it.startTime || !it.endTime) {
            ElMessage.warning("申报段起止时段不能为空");
            return;
        }
    }
    const payload = {
        tradeDate: form.tradeDate,
        marketType: form.marketType,
        stage: form.stage,
        items: form.items,
    };
    const res: any = await $api.post(`/trade/declarations`, payload);
    if (res.code === 0) {
        createResult.value = res.data;
        ElMessage.success("申报单已创建（合规预检完成）");
        load();
    }
};

// ---- 提交申报 ----
const submitDecl = (row: any) => {
    ElMessageBox.confirm(
        `确定将申报单「${row.declarationNo}」提交至交易中心吗？提交后不可撤回。`,
        "提示",
        { type: "warning" }
    ).then(async () => {
        const res: any = await $api.post(`/trade/declarations/${row.id}/submit`);
        if (res.code === 0) {
            ElMessage.success("提交成功，回执号：" + (res.data?.receiptNo || ""));
            load();
        }
    });
};

// ---- 成交结果 ----
const results = ref<any[]>([]);
const resultQuery = reactive({ tradeDate: "", marketType: "" });
const resultLoading = ref(false);

const loadResults = async () => {
    if (!resultQuery.tradeDate) {
        ElMessage.warning("请选择交易日");
        return;
    }
    resultLoading.value = true;
    try {
        const params: any = { tradeDate: resultQuery.tradeDate };
        if (resultQuery.marketType) params.marketType = resultQuery.marketType;
        const res: any = await $api.get(`/trade/results`, { params });
        if (res.code === 0) {
            results.value = res.data || [];
        }
    } finally {
        resultLoading.value = false;
    }
};

const resultStatusLabel = (s: string) => {
    const map: Record<string, string> = {
        settled: "已结算",
        pending: "待结算",
        failed: "失败",
    };
    return map[s] || s;
};

// ---- 持仓曲线 ----
const posDate = ref("");
const posChartRef = ref<HTMLDivElement>();
let posChart: echarts.ECharts | null = null;
const posLoading = ref(false);

const loadPositions = async () => {
    if (!posDate.value) {
        ElMessage.warning("请选择持仓日期");
        return;
    }
    posLoading.value = true;
    try {
        const res: any = await $api.get(`/trade/positions`, {
            params: { tradeDate: posDate.value },
        });
        if (res.code === 0) {
            nextTick(() => renderPosChart(res.data || {}));
        }
    } finally {
        posLoading.value = false;
    }
};

const renderPosChart = (data: any) => {
    if (!posChartRef.value) return;
    if (!posChart) posChart = echarts.init(posChartRef.value);
    const times = Array.from({ length: 96 }, (_, i) => {
        const h = String(Math.floor(i / 4)).padStart(2, "0");
        const m = String((i % 4) * 15).padStart(2, "0");
        return h + ":" + m;
    });
    posChart.setOption({
        tooltip: { trigger: "axis" },
        legend: { data: ["中长期持仓", "现货持仓", "净持仓"] },
        grid: { left: 60, right: 30, top: 40, bottom: 30 },
        xAxis: { type: "category", data: times },
        yAxis: { type: "value", name: "MW" },
        series: [
            { name: "中长期持仓", type: "line", smooth: true, showSymbol: false, data: data.longTerm || [] },
            { name: "现货持仓", type: "line", smooth: true, showSymbol: false, data: data.spot || [] },
            { name: "净持仓", type: "line", smooth: true, showSymbol: false, data: data.net || [], areaStyle: { opacity: 0.1 } },
        ],
    });
};

const handleResize = () => posChart?.resize();

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
    pageQuery.tradeDate = today();
    resultQuery.tradeDate = today();
    posDate.value = today();
    load();
    loadResults();
    loadPositions();
    window.addEventListener("resize", handleResize);
});

onBeforeUnmount(() => {
    window.removeEventListener("resize", handleResize);
    posChart?.dispose();
});

const onTabChange = (name: string | number) => {
    if (name === "results") loadResults();
    if (name === "positions") loadPositions();
};
</script>

<template>
    <div class="page">
        <el-card shadow="never">
            <el-tabs v-model="activeTab" @tab-change="onTabChange">
                <!-- 申报单 -->
                <el-tab-pane label="申报单" name="declaration">
                    <div class="toolbar">
                        <el-form inline @submit.prevent>
                            <el-form-item label="交易日期">
                                <el-date-picker
                                    v-model="pageQuery.tradeDate"
                                    type="date"
                                    value-format="YYYY-MM-DD"
                                    placeholder="全部"
                                    style="width: 140px"
                                />
                            </el-form-item>
                            <el-form-item label="状态">
                                <el-select v-model="pageQuery.status" clearable placeholder="全部" style="width: 120px">
                                    <el-option v-for="o in statusOptions" :key="o.value" :value="o.value" :label="o.label" />
                                </el-select>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="load">查询</el-button>
                                <el-button type="success" @click="openCreate">新建申报</el-button>
                            </el-form-item>
                        </el-form>
                    </div>

                    <el-table :data="list" v-loading="loading" border stripe>
                        <el-table-column prop="declarationNo" label="申报单号" width="180" />
                        <el-table-column prop="tradeDate" label="交易日期" width="110">
                            <template #default="{ row }">
                                {{ String(row.tradeDate || "").substring(0, 10) }}
                            </template>
                        </el-table-column>
                        <el-table-column label="市场" width="100">
                            <template #default="{ row }">
                                {{ marketTypeOptions.find((o) => o.value === row.marketType)?.label || row.marketType }}
                            </template>
                        </el-table-column>
                        <el-table-column label="阶段" width="90">
                            <template #default="{ row }">
                                {{ stageOptions.find((o) => o.value === row.stage)?.label || row.stage }}
                            </template>
                        </el-table-column>
                        <el-table-column label="申报段数" width="90">
                            <template #default="{ row }">
                                {{ segmentsOf(row) }} 段
                            </template>
                        </el-table-column>
                        <el-table-column label="合规预检" width="100">
                            <template #default="{ row }">
                                <el-tag
                                    :type="compliancePassed(row) ? 'success' : 'danger'"
                                    size="small"
                                >
                                    {{ compliancePassed(row) ? "通过" : "未通过" }}
                                </el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column label="状态" width="95">
                            <template #default="{ row }">
                                <el-tag
                                    :type="row.status === 'submitted' ? 'success' : row.status === 'draft' ? 'info' : 'warning'"
                                    size="small"
                                >
                                    {{ statusLabel(row.status) }}
                                </el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="receiptNo" label="回执号" width="170" />
                        <el-table-column prop="createdBy" label="创建人" width="100" />
                        <el-table-column label="操作" width="200" fixed="right">
                            <template #default="{ row }">
                                <el-button type="primary" link @click="viewCheck(row)">合规明细</el-button>
                                <el-button
                                    v-if="row.status === 'pending_submit' || row.status === 'draft'"
                                    type="success"
                                    link
                                    @click="submitDecl(row)"
                                >
                                    提交
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

                <!-- 成交结果 -->
                <el-tab-pane label="成交结果" name="results">
                    <div class="toolbar">
                        <el-form inline @submit.prevent>
                            <el-form-item label="交易日期">
                                <el-date-picker
                                    v-model="resultQuery.tradeDate"
                                    type="date"
                                    value-format="YYYY-MM-DD"
                                    placeholder="选择日期"
                                    style="width: 140px"
                                />
                            </el-form-item>
                            <el-form-item label="市场">
                                <el-select v-model="resultQuery.marketType" clearable placeholder="全部" style="width: 120px">
                                    <el-option v-for="o in marketTypeOptions" :key="o.value" :value="o.value" :label="o.label" />
                                </el-select>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="loadResults">查询</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                    <el-table :data="results" v-loading="resultLoading" border stripe>
                        <el-table-column prop="declarationId" label="申报单 ID" width="190" />
                        <el-table-column prop="tradeDate" label="交易日期" width="110">
                            <template #default="{ row }">
                                {{ String(row.tradeDate || "").substring(0, 10) }}
                            </template>
                        </el-table-column>
                        <el-table-column prop="matchedVolume" label="成交量（MWh）" width="130" />
                        <el-table-column prop="matchedPrice" label="成交价（元/MWh）" width="140" />
                        <el-table-column label="状态" width="100">
                            <template #default="{ row }">
                                <el-tag size="small">{{ resultStatusLabel(row.status) }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="settlementImpact" label="结算影响" min-width="160" />
                    </el-table>
                </el-tab-pane>

                <!-- 持仓曲线 -->
                <el-tab-pane label="持仓曲线" name="positions">
                    <div class="toolbar">
                        <el-form inline @submit.prevent>
                            <el-form-item label="持仓日期">
                                <el-date-picker
                                    v-model="posDate"
                                    type="date"
                                    value-format="YYYY-MM-DD"
                                    placeholder="选择日期"
                                    style="width: 140px"
                                />
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="loadPositions">查询</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                    <div ref="posChartRef" class="chart" v-loading="posLoading"></div>
                </el-tab-pane>
            </el-tabs>
        </el-card>

        <!-- 新建申报弹窗 -->
        <el-dialog v-model="dialogVisible" title="新建交易申报" width="760px" destroy-on-close>
            <el-form ref="formRef" :model="form" label-width="90px">
                <el-form-item label="交易日期" required>
                    <el-date-picker
                        v-model="form.tradeDate"
                        type="date"
                        value-format="YYYY-MM-DD"
                        placeholder="选择交易日期"
                        style="width: 200px"
                    />
                </el-form-item>
                <el-form-item label="市场类型" required>
                    <el-select v-model="form.marketType" style="width: 200px">
                        <el-option v-for="o in marketTypeOptions" :key="o.value" :value="o.value" :label="o.label" />
                    </el-select>
                </el-form-item>
                <el-form-item label="申报阶段" required>
                    <el-select v-model="form.stage" style="width: 200px">
                        <el-option v-for="o in stageOptions" :key="o.value" :value="o.value" :label="o.label" />
                    </el-select>
                </el-form-item>

                <el-form-item label="申报段">
                    <div class="items-box">
                        <div v-for="(it, idx) in form.items" :key="idx" class="item-row">
                            <span class="item-index">{{ idx + 1 }}</span>
                            <el-time-select
                                v-model="it.startTime"
                                start="00:00"
                                step="00:15"
                                end="23:45"
                                placeholder="开始"
                                style="width: 110px"
                            />
                            <span class="sep">—</span>
                            <el-time-select
                                v-model="it.endTime"
                                start="00:15"
                                step="00:15"
                                end="24:00"
                                placeholder="结束"
                                style="width: 110px"
                            />
                            <el-input-number v-model="it.volume" :min="0" :max="99999" placeholder="量(MWh)" style="width: 130px" controls-position="right" />
                            <el-input-number v-model="it.price" :min="0" :max="9999" placeholder="价(元/MWh)" style="width: 150px" controls-position="right" />
                            <el-button type="danger" link @click="removeItem(idx)">删除</el-button>
                        </div>
                        <el-button type="primary" link @click="addItem">+ 添加申报段（≤10 段）</el-button>
                    </div>
                </el-form-item>
            </el-form>

            <!-- 合规预检结果 -->
            <el-alert
                v-if="createResult"
                class="check-alert"
                :type="createResult.complianceCheck?.passed ? 'success' : 'error'"
                :title="createResult.complianceCheck?.passed ? '合规预检通过，申报单状态：待提交' : '合规预检未通过，申报单状态：草稿（需修改后重新提交）'"
                :closable="false"
                show-icon
            >
                <div v-if="createResult.complianceCheck?.violations?.length" class="violations">
                    <div v-for="(v, i) in createResult.complianceCheck.violations" :key="i" class="violation-item">
                        {{ v }}
                    </div>
                </div>
                <div v-if="createResult.declarationId" class="decl-id">
                    申报单号：{{ createResult.declarationId }}
                </div>
            </el-alert>

            <template #footer>
                <el-button @click="dialogVisible = false">关闭</el-button>
                <el-button type="primary" @click="submitCreate">提交预检</el-button>
            </template>
        </el-dialog>

        <!-- 合规明细弹窗 -->
        <el-dialog v-model="checkVisible" title="合规预检明细" width="640px">
            <el-descriptions :column="1" border>
                <el-descriptions-item label="申报段数">
                    {{ checkDetail?.segments }} / 上限 {{ checkDetail?.maxSegments }}
                </el-descriptions-item>
                <el-descriptions-item label="价格区间">
                    {{ checkDetail?.priceRange?.[0] }} ~ {{ checkDetail?.priceRange?.[1] }} 元/MWh
                </el-descriptions-item>
                <el-descriptions-item label="总量">
                    {{ checkDetail?.totalVolume }} MWh
                </el-descriptions-item>
                <el-descriptions-item label="结论">
                    <el-tag :type="checkDetail?.passed ? 'success' : 'danger'" size="small">
                        {{ checkDetail?.passed ? "通过" : "未通过" }}
                    </el-tag>
                </el-descriptions-item>
            </el-descriptions>
            <div v-if="checkDetail?.violations?.length" class="violations">
                <div class="violation-title">违规项：</div>
                <div v-for="(v, i) in checkDetail.violations" :key="i" class="violation-item">
                    {{ v }}
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<style scoped lang="scss">
.page {
    padding: 16px;

    .toolbar {
        margin-bottom: 12px;
    }

    .pager {
        margin-top: 12px;
        justify-content: flex-end;
    }

    .chart {
        width: 100%;
        height: 380px;
    }

    .items-box {
        width: 100%;

        .item-row {
            display: flex;
            align-items: center;
            gap: 6px;
            margin-bottom: 8px;

            .item-index {
                width: 18px;
                color: #838c99;
                font-size: 12px;
            }

            .sep {
                color: #c0c4cc;
            }
        }
    }

    .check-alert {
        margin-top: 12px;

        .violations {
            margin-top: 6px;

            .violation-item {
                font-size: 12px;
                line-height: 20px;
            }
        }

        .decl-id {
            margin-top: 6px;
            font-size: 12px;
            font-weight: 600;
        }
    }

    .violation-title {
        font-weight: 600;
        margin-top: 10px;
    }
}
</style>
