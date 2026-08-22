<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from "vue";
import * as echarts from "echarts";
import $api from "@/api/Axios";

// PTIDSS 市场行情（对齐 OpenAPI V1.0 /market/**：现货价格/中长期价格/供需形势/量价热力图）

const loading = ref(false);

// ---- 现货价格（96 点曲线）----
const spotForm = reactive({
    marketType: "intra_province",
    stage: "day_ahead",
    tradeDate: "",
});
const spotChartRef = ref<HTMLDivElement>();
let spotChart: echarts.ECharts | null = null;

const marketTypeOptions = [
    { value: "intra_province", label: "省内市场" },
    { value: "inter_province", label: "省间市场" },
];
const stageOptions = [
    { value: "day_ahead", label: "日前" },
    { value: "real_time", label: "实时" },
    { value: "rolling", label: "滚动" },
];

const spotData = ref<any[]>([]);
const loadSpot = async () => {
    loading.value = true;
    try {
        const date = spotForm.tradeDate || "";
        const params: any = {
            marketType: spotForm.marketType,
            stage: spotForm.stage,
        };
        if (date) {
            params.startAt = date + " 00:00:00";
            params.endAt = date + " 23:59:59";
        }
        const res: any = await $api.get(`/market/price/spot`, { params });
        if (res.code === 0) {
            spotData.value = res.data || [];
            renderSpotChart();
        }
    } finally {
        loading.value = false;
    }
};

const renderSpotChart = () => {
    if (!spotChartRef.value) return;
    if (!spotChart) spotChart = echarts.init(spotChartRef.value);
    const times = spotData.value.map((p: any) => {
        const t = String(p.ts || "");
        return t.length >= 16 ? t.substring(11, 16) : t;
    });
    const prices = spotData.value.map((p: any) => p.price);
    const volumes = spotData.value.map((p: any) => p.volume);
    spotChart.setOption({
        tooltip: { trigger: "axis" },
        legend: { data: ["价格（元/MWh）", "成交量（MWh）"] },
        grid: { left: 50, right: 60, top: 40, bottom: 30 },
        xAxis: { type: "category", data: times, name: "时段（15min）" },
        yAxis: [
            { type: "value", name: "价格（元/MWh）" },
            { type: "value", name: "成交量（MWh）", splitLine: { show: false } },
        ],
        series: [
            {
                name: "价格（元/MWh）",
                type: "line",
                smooth: true,
                showSymbol: false,
                data: prices,
                areaStyle: { opacity: 0.12 },
            },
            {
                name: "成交量（MWh）",
                type: "bar",
                yAxisIndex: 1,
                barWidth: "60%",
                itemStyle: { opacity: 0.35 },
                data: volumes,
            },
        ],
    });
};

// ---- 中长期成交价格 ----
const midlongForm = reactive({ variety: "monthly", startDate: "" });
const midlongChartRef = ref<HTMLDivElement>();
let midlongChart: echarts.ECharts | null = null;

const varietyOptions = [
    { value: "weekly", label: "周度" },
    { value: "monthly", label: "月度" },
    { value: "annual", label: "年度" },
];

const midlongData = ref<any[]>([]);
const loadMidlong = async () => {
    const params: any = { variety: midlongForm.variety };
    if (midlongForm.startDate) {
        params.startAt = midlongForm.startDate + " 00:00:00";
    }
    const res: any = await $api.get(`/market/price/midlong`, { params });
    if (res.code === 0) {
        midlongData.value = res.data || [];
        renderMidlongChart();
    }
};

const renderMidlongChart = () => {
    if (!midlongChartRef.value) return;
    if (!midlongChart) midlongChart = echarts.init(midlongChartRef.value);
    const times = midlongData.value.map((p: any) => String(p.ts || "").substring(0, 10));
    const prices = midlongData.value.map((p: any) => p.price);
    midlongChart.setOption({
        tooltip: { trigger: "axis" },
        legend: { data: ["成交均价（元/MWh）"] },
        grid: { left: 50, right: 30, top: 40, bottom: 30 },
        xAxis: { type: "category", data: times },
        yAxis: { type: "value", name: "元/MWh" },
        series: [
            {
                name: "成交均价（元/MWh）",
                type: "line",
                smooth: true,
                showSymbol: true,
                data: prices,
            },
        ],
    });
};

// ---- 供需形势 ----
const supplyDate = ref("");
const supplyChartRef = ref<HTMLDivElement>();
let supplyChart: echarts.ECharts | null = null;

const supplyData = ref<any[]>([]);
const loadSupply = async () => {
    const params: any = {};
    if (supplyDate.value) {
        params.startAt = supplyDate.value + " 00:00:00";
        params.endAt = supplyDate.value + " 23:59:59";
    }
    const res: any = await $api.get(`/market/supply-demand`, { params });
    if (res.code === 0) {
        supplyData.value = res.data || [];
        renderSupplyChart();
    }
};

const renderSupplyChart = () => {
    if (!supplyChartRef.value) return;
    if (!supplyChart) supplyChart = echarts.init(supplyChartRef.value);
    const times = supplyData.value.map((p: any) => String(p.ts || "").substring(11, 16));
    const load = supplyData.value.map((p: any) => p.loadValue);
    const cap = supplyData.value.map((p: any) => p.availableCapacity);
    const renew = supplyData.value.map((p: any) => p.renewableOutput);
    supplyChart.setOption({
        tooltip: { trigger: "axis" },
        legend: { data: ["系统负荷", "可用能力", "新能源出力"] },
        grid: { left: 50, right: 30, top: 40, bottom: 30 },
        xAxis: { type: "category", data: times },
        yAxis: { type: "value", name: "MW" },
        series: [
            { name: "系统负荷", type: "line", smooth: true, showSymbol: false, data: load },
            { name: "可用能力", type: "line", smooth: true, showSymbol: false, data: cap },
            {
                name: "新能源出力",
                type: "line",
                smooth: true,
                showSymbol: false,
                data: renew,
                areaStyle: { opacity: 0.1 },
            },
        ],
    });
};

// ---- 量价热力图（日期 × 96 时段）----
const heatForm = reactive({ startDate: "", days: 3 });
const heatChartRef = ref<HTMLDivElement>();
let heatChart: echarts.ECharts | null = null;

const loadHeatmap = async () => {
    if (!heatForm.startDate) {
        return;
    }
    const end = new Date(heatForm.startDate);
    end.setDate(end.getDate() + heatForm.days - 1);
    const fmt = (d: Date) =>
        d.getFullYear() +
        "-" +
        String(d.getMonth() + 1).padStart(2, "0") +
        "-" +
        String(d.getDate()).padStart(2, "0");
    const res: any = await $api.get(`/market/heatmap`, {
        params: { startDate: heatForm.startDate, endDate: fmt(end) },
    });
    if (res.code === 0) {
        renderHeatChart(res.data || {});
    }
};

const renderHeatChart = (data: any) => {
    if (!heatChartRef.value) return;
    if (!heatChart) heatChart = echarts.init(heatChartRef.value);
    const dates: string[] = data.dates || [];
    const points: number[] = data.points || [];
    const perDay = 96;
    const values: any[] = [];
    points.forEach((price: number, i: number) => {
        const y = Math.floor(i / perDay);
        const x = i % perDay;
        values.push([x, y, price]);
    });
    heatChart.setOption({
        tooltip: {
            position: "top",
            formatter: (p: any) => {
                const d = dates[p.value[1]] || "";
                const h = String(Math.floor(p.value[0] / 4)).padStart(2, "0");
                const m = String((p.value[0] % 4) * 15).padStart(2, "0");
                return d + " " + h + ":" + m + "<br/>价格：" + p.value[2] + " 元/MWh";
            },
        },
        grid: { left: 80, right: 30, top: 10, bottom: 50 },
        xAxis: {
            type: "category",
            data: Array.from({ length: 96 }, (_, i) => i),
            name: "时段",
        },
        yAxis: { type: "category", data: dates, name: "日期" },
        visualMap: {
            min: 0,
            max: 800,
            calculable: true,
            orient: "horizontal",
            left: "center",
            bottom: 0,
            inRange: { color: ["#3b6fd4", "#7ad0f5", "#ffe08a", "#f56c6c"] },
        },
        series: [
            {
                type: "heatmap",
                data: values,
                emphasis: { itemStyle: { borderColor: "#333", borderWidth: 1 } },
            },
        ],
    });
};

// 默认日期：今天
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

const handleResize = () => {
    spotChart?.resize();
    midlongChart?.resize();
    supplyChart?.resize();
    heatChart?.resize();
};

onMounted(async () => {
    spotForm.tradeDate = today();
    midlongForm.startDate = today();
    supplyDate.value = today();
    heatForm.startDate = today();
    await loadSpot();
    await loadMidlong();
    await loadSupply();
    await loadHeatmap();
    window.addEventListener("resize", handleResize);
});

onBeforeUnmount(() => {
    window.removeEventListener("resize", handleResize);
    spotChart?.dispose();
    midlongChart?.dispose();
    supplyChart?.dispose();
    heatChart?.dispose();
});
</script>

<template>
    <div class="page">
        <el-card shadow="never" class="block-card">
            <template #header>
                <div class="card-head">
                    <span class="card-title">现货价格曲线（96 点）</span>
                    <div class="card-tools">
                        <el-select v-model="spotForm.marketType" style="width: 120px" @change="loadSpot">
                            <el-option v-for="o in marketTypeOptions" :key="o.value" :value="o.value" :label="o.label" />
                        </el-select>
                        <el-select v-model="spotForm.stage" style="width: 110px" @change="loadSpot">
                            <el-option v-for="o in stageOptions" :key="o.value" :value="o.value" :label="o.label" />
                        </el-select>
                        <el-date-picker
                            v-model="spotForm.tradeDate"
                            type="date"
                            value-format="YYYY-MM-DD"
                            placeholder="交易日期"
                            style="width: 140px"
                            @change="loadSpot"
                        />
                    </div>
                </div>
            </template>
            <div ref="spotChartRef" class="chart" v-loading="loading"></div>
        </el-card>

        <el-card shadow="never" class="block-card">
            <template #header>
                <div class="card-head">
                    <span class="card-title">中长期成交价格</span>
                    <div class="card-tools">
                        <el-select v-model="midlongForm.variety" style="width: 110px" @change="loadMidlong">
                            <el-option v-for="o in varietyOptions" :key="o.value" :value="o.value" :label="o.label" />
                        </el-select>
                        <el-date-picker
                            v-model="midlongForm.startDate"
                            type="date"
                            value-format="YYYY-MM-DD"
                            placeholder="起始日期"
                            style="width: 140px"
                            @change="loadMidlong"
                        />
                    </div>
                </div>
            </template>
            <div ref="midlongChartRef" class="chart"></div>
        </el-card>

        <el-card shadow="never" class="block-card">
            <template #header>
                <div class="card-head">
                    <span class="card-title">供需形势</span>
                    <div class="card-tools">
                        <el-date-picker
                            v-model="supplyDate"
                            type="date"
                            value-format="YYYY-MM-DD"
                            placeholder="日期"
                            style="width: 140px"
                            @change="loadSupply"
                        />
                    </div>
                </div>
            </template>
            <div ref="supplyChartRef" class="chart"></div>
        </el-card>

        <el-card shadow="never" class="block-card">
            <template #header>
                <div class="card-head">
                    <span class="card-title">量价热力图（日期 × 96 时段）</span>
                    <div class="card-tools">
                        <el-date-picker
                            v-model="heatForm.startDate"
                            type="date"
                            value-format="YYYY-MM-DD"
                            placeholder="起始日期"
                            style="width: 140px"
                        />
                        <el-select v-model="heatForm.days" style="width: 100px">
                            <el-option :value="1" label="1 天" />
                            <el-option :value="3" label="3 天" />
                            <el-option :value="7" label="7 天" />
                        </el-select>
                        <el-button type="primary" @click="loadHeatmap">生成</el-button>
                    </div>
                </div>
            </template>
            <div ref="heatChartRef" class="chart chart-heat"></div>
        </el-card>
    </div>
</template>

<style scoped lang="scss">
.page {
    padding: 16px;

    .block-card {
        margin-bottom: 16px;
    }

    .card-head {
        display: flex;
        align-items: center;
        justify-content: space-between;

        .card-title {
            font-weight: 600;
            color: #1f3b6b;
        }

        .card-tools {
            display: flex;
            gap: 8px;
        }
    }

    .chart {
        width: 100%;
        height: 320px;
    }

    .chart-heat {
        height: 420px;
    }
}
</style>
