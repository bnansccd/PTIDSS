<script setup lang="ts">
import { reactive, ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import $api from "@/api/Axios";

// PTIDSS 情报中心（intel_source 情报源台账 + intel_news 情报流）
// 参考原型 intel-center.html：4 统计卡 + 情报源管理 + 情报流列表

interface IntelSource {
    id: string;
    sourceCode: string;
    sourceName: string;
    intelType: string;
    fetchMode: string;
    frequency: string;
    status: string;
}

interface IntelNews {
    id: string;
    title: string;
    importance: string;
    publishedAt: string;
    regionCode?: string;
    sourceCode?: string;
    normalizedTags?: string[] | string;
}

const loading = ref(false);
const activeTab = ref("news");
const sources = ref<IntelSource[]>([]);
const newsList = ref<IntelNews[]>([]);
const newsTotal = ref(0);
const newsQuery = reactive({ pageNum: 1, pageSize: 10, importance: "", intelType: "" });

const stats = computed(() => ({
    total: sources.value.length,
    online: sources.value.filter((s) => s.status === "enabled").length,
    newsTotal: newsTotal.value,
    high: newsList.value.filter((n) => n.importance === "high").length,
}));

const typeOptions = [
    { value: "price", label: "价格行情" },
    { value: "weather", label: "气象信息" },
    { value: "supply_demand", label: "供需信息" },
    { value: "policy", label: "政策动态" },
    { value: "announcement", label: "公告披露" },
    { value: "opinion", label: "舆情观点" },
];
const fetchOptions = [
    { value: "api", label: "API 对接" },
    { value: "crawl", label: "爬虫采集" },
    { value: "file", label: "文件导入" },
];
const importanceOptions = [
    { value: "high", label: "high" },
    { value: "medium", label: "medium" },
    { value: "low", label: "low" },
];

const typeLabel = (v: string) => typeOptions.find((o) => o.value === v)?.label || v;
const fetchLabel = (v: string) => fetchOptions.find((o) => o.value === v)?.label || v;
const importanceTag = (v: string) =>
    v === "high" ? "danger" : v === "medium" ? "warning" : "info";

const loadSources = async () => {
    const res: any = await $api.get(`/intel/sources`);
    if (res.code === 0) sources.value = res.data || [];
};

const loadNews = async () => {
    loading.value = true;
    try {
        const res: any = await $api.get(`/intel/news`, { params: { ...newsQuery } });
        if (res.code === 0) {
            newsList.value = res.data.records || [];
            newsTotal.value = res.data.total || 0;
        }
    } finally {
        loading.value = false;
    }
};

// ── 新建情报源 ──
const dialogVisible = ref(false);
const formRef = ref();
const form = reactive<any>({
    sourceCode: "",
    sourceName: "",
    intelType: "price",
    fetchMode: "api",
    frequency: "0 */5 * * * *",
    status: "enabled",
});
const rules = {
    sourceCode: [{ required: true, message: "请输入情报源编码", trigger: "blur" }],
    sourceName: [{ required: true, message: "请输入情报源名称", trigger: "blur" }],
    intelType: [{ required: true, message: "请选择情报类型", trigger: "change" }],
};

const openCreate = () => {
    Object.assign(form, {
        sourceCode: "",
        sourceName: "",
        intelType: "price",
        fetchMode: "api",
        frequency: "0 */5 * * * *",
        status: "enabled",
    });
    dialogVisible.value = true;
};

const submit = async () => {
    await formRef.value.validate();
    const res: any = await $api.post(`/intel/sources`, { ...form });
    if (res.code === 0) {
        ElMessage.success(`情报源登记成功（ID ${res.data.id}）`);
        dialogVisible.value = false;
        loadSources();
    }
};

onMounted(() => {
    loadSources();
    loadNews();
});
</script>

<template>
    <div class="page">
        <!-- 4 统计卡（对齐原型 intel-center.html） -->
        <el-row :gutter="16" class="stat-row">
            <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                    <div class="stat-label">情报源</div>
                    <div class="stat-value">{{ stats.total }}</div>
                    <div class="stat-tip">intel_source 台账</div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                    <div class="stat-label">在线情报源</div>
                    <div class="stat-value ok">{{ stats.online }}</div>
                    <div class="stat-tip">status=enabled</div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                    <div class="stat-label">情报流总量</div>
                    <div class="stat-value">{{ stats.newsTotal }}</div>
                    <div class="stat-tip">intel_news 累计</div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                    <div class="stat-label">high 级情报</div>
                    <div class="stat-value danger">{{ stats.high }}</div>
                    <div class="stat-tip">本页 high 重要度</div>
                </el-card>
            </el-col>
        </el-row>

        <el-card shadow="never">
            <el-tabs v-model="activeTab">
                <el-tab-pane label="情报流" name="news">
                    <div class="toolbar">
                        <span class="tip-text">高价值情报按标签 × 重要度推送（high 级自动短信/小程序）</span>
                        <div>
                            <el-select v-model="newsQuery.importance" clearable placeholder="重要度" style="width: 120px; margin-right: 8px">
                                <el-option v-for="o in importanceOptions" :key="o.value" :value="o.value" :label="o.label" />
                            </el-select>
                            <el-select v-model="newsQuery.intelType" clearable placeholder="情报类型" style="width: 130px; margin-right: 8px">
                                <el-option v-for="o in typeOptions" :key="o.value" :value="o.value" :label="o.label" />
                            </el-select>
                            <el-button type="primary" @click="loadNews">查询</el-button>
                        </div>
                    </div>
                    <el-table :data="newsList" v-loading="loading" border stripe>
                        <el-table-column label="重要度" width="90">
                            <template #default="{ row }">
                                <el-tag :type="importanceTag(row.importance)" size="small">{{ row.importance }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="title" label="情报标题" min-width="260" show-overflow-tooltip />
                        <el-table-column label="类型" width="110">
                            <template #default="{ row }">{{ typeLabel(row.intelType) }}</template>
                        </el-table-column>
                        <el-table-column prop="regionCode" label="区域" width="90">
                            <template #default="{ row }"><span class="mono">{{ row.regionCode || '—' }}</span></template>
                        </el-table-column>
                        <el-table-column prop="sourceCode" label="来源" width="140">
                            <template #default="{ row }"><span class="mono">{{ row.sourceCode || '—' }}</span></template>
                        </el-table-column>
                        <el-table-column prop="publishedAt" label="发布时间" width="170">
                            <template #default="{ row }"><span class="mono">{{ row.publishedAt }}</span></template>
                        </el-table-column>
                    </el-table>
                    <el-pagination
                        style="margin-top: 12px; justify-content: flex-end"
                        layout="total, prev, pager, next"
                        :total="newsTotal"
                        :page-size="newsQuery.pageSize"
                        :current-page="newsQuery.pageNum"
                        @current-change="(p: number) => { newsQuery.pageNum = p; loadNews(); }"
                    />
                </el-tab-pane>

                <el-tab-pane label="情报源管理" name="sources">
                    <div class="toolbar">
                        <span class="tip-text">情报采集源台账：API / 爬虫 / 文件三通道</span>
                        <div>
                            <el-button type="primary" @click="openCreate">新增情报源</el-button>
                        </div>
                    </div>
                    <el-table :data="sources" v-loading="loading" border stripe>
                        <el-table-column prop="sourceCode" label="源编码" width="170">
                            <template #default="{ row }"><span class="mono">{{ row.sourceCode }}</span></template>
                        </el-table-column>
                        <el-table-column prop="sourceName" label="情报源名称" min-width="180" />
                        <el-table-column label="情报类型" width="110">
                            <template #default="{ row }">
                                <el-tag size="small">{{ typeLabel(row.intelType) }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column label="采集方式" width="110">
                            <template #default="{ row }">{{ fetchLabel(row.fetchMode) }}</template>
                        </el-table-column>
                        <el-table-column prop="frequency" label="采集频率" min-width="140">
                            <template #default="{ row }"><span class="mono">{{ row.frequency || '—' }}</span></template>
                        </el-table-column>
                        <el-table-column label="状态" width="90">
                            <template #default="{ row }">
                                <el-tag :type="row.status === 'enabled' ? 'success' : 'info'" size="small">
                                    {{ row.status }}
                                </el-tag>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>
            </el-tabs>
        </el-card>

        <el-dialog v-model="dialogVisible" title="新增情报源" width="560px" destroy-on-close>
            <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
                <el-form-item label="源编码" prop="sourceCode">
                    <el-input v-model="form.sourceCode" placeholder="如 INTL-SZSE-01" />
                </el-form-item>
                <el-form-item label="情报源名称" prop="sourceName">
                    <el-input v-model="form.sourceName" placeholder="如 深交所公告" />
                </el-form-item>
                <el-form-item label="情报类型" prop="intelType">
                    <el-select v-model="form.intelType" style="width: 100%">
                        <el-option v-for="o in typeOptions" :key="o.value" :value="o.value" :label="o.label" />
                    </el-select>
                </el-form-item>
                <el-form-item label="采集方式">
                    <el-select v-model="form.fetchMode" style="width: 100%">
                        <el-option v-for="o in fetchOptions" :key="o.value" :value="o.value" :label="o.label" />
                    </el-select>
                </el-form-item>
                <el-form-item label="采集频率">
                    <el-input v-model="form.frequency" placeholder="cron 表达式，如 0 */5 * * * *" />
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="form.status" style="width: 100%">
                        <el-option label="启用" value="enabled" />
                        <el-option label="停用" value="disabled" />
                    </el-select>
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
