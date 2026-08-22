<script setup lang="ts">
import { reactive, ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import $api from "@/api/Axios";

// PTIDSS 政策中心（policy_document 政策库：国家/区域/省级分类 + 上传登记）
// 参考原型 policy-detail.html：筛选条 + 上传弹窗 + 政策列表

interface PolicyDoc {
    id: string;
    title: string;
    issuingBody: string;
    category: string;
    tags?: string[] | string;
    versionNo?: number;
    fileUrl?: string;
    publishDate?: string;
    effectiveDate?: string;
    status: string;
}

const loading = ref(false);
const list = ref<PolicyDoc[]>([]);
const total = ref(0);
const pageQuery = reactive({ pageNum: 1, pageSize: 10, category: "", keyword: "", status: "" });

const stats = computed(() => ({
    total: total.value,
    published: list.value.filter((p) => p.status === "published").length,
    draft: list.value.filter((p) => p.status === "draft").length,
    expired: list.value.filter((p) => p.status === "expired").length,
}));

const categoryOptions = [
    { value: "national", label: "国家级" },
    { value: "regional", label: "区域级" },
    { value: "provincial", label: "省级" },
];
const statusOptions = [
    { value: "draft", label: "草稿" },
    { value: "published", label: "已发布" },
    { value: "expired", label: "已过期" },
];

const categoryLabel = (v: string) => categoryOptions.find((o) => o.value === v)?.label || v;

const load = async () => {
    loading.value = true;
    try {
        const res: any = await $api.get(`/policy/list`, { params: { ...pageQuery } });
        if (res.code === 0) {
            list.value = res.data.records || [];
            total.value = res.data.total || 0;
        }
    } finally {
        loading.value = false;
    }
};

// ── 上传新政策 ──
const dialogVisible = ref(false);
const formRef = ref();
const form = reactive<any>({
    title: "",
    issuingBody: "",
    category: "national",
    tagsText: "",
    publishDate: "",
    effectiveDate: "",
    status: "published",
    fileUrl: "",
});
const rules = {
    title: [{ required: true, message: "请输入政策标题", trigger: "blur" }],
    category: [{ required: true, message: "请选择政策分类", trigger: "change" }],
};

const openUpload = () => {
    Object.assign(form, {
        title: "",
        issuingBody: "",
        category: "national",
        tagsText: "",
        publishDate: "",
        effectiveDate: "",
        status: "published",
        fileUrl: "",
    });
    dialogVisible.value = true;
};

const submit = async () => {
    await formRef.value.validate();
    const payload: any = {
        title: form.title,
        issuingBody: form.issuingBody || undefined,
        category: form.category,
        publishDate: form.publishDate || undefined,
        effectiveDate: form.effectiveDate || undefined,
        status: form.status,
        fileUrl: form.fileUrl || undefined,
    };
    if (form.tagsText.trim()) {
        payload.tags = form.tagsText.split(/[,，]/).map((t: string) => t.trim()).filter(Boolean);
    }
    const res: any = await $api.post(`/policy/upload`, payload);
    if (res.code === 0) {
        ElMessage.success(`政策上传成功（ID ${res.data.id}）`);
        dialogVisible.value = false;
        load();
    }
};

onMounted(load);
</script>

<template>
    <div class="page">
        <!-- 统计卡 -->
        <el-row :gutter="16" class="stat-row">
            <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                    <div class="stat-label">政策总量</div>
                    <div class="stat-value">{{ stats.total }}</div>
                    <div class="stat-tip">policy_document 全量</div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                    <div class="stat-label">已发布</div>
                    <div class="stat-value ok">{{ stats.published }}</div>
                    <div class="stat-tip">本页已发布</div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                    <div class="stat-label">草稿</div>
                    <div class="stat-value">{{ stats.draft }}</div>
                    <div class="stat-tip">本页草稿</div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                    <div class="stat-label">已过期</div>
                    <div class="stat-value danger">{{ stats.expired }}</div>
                    <div class="stat-tip">本页已过期</div>
                </el-card>
            </el-col>
        </el-row>

        <el-card shadow="never">
            <div class="toolbar">
                <el-form inline @submit.prevent>
                    <el-form-item label="分类">
                        <el-select v-model="pageQuery.category" clearable placeholder="全部" style="width: 120px">
                            <el-option v-for="o in categoryOptions" :key="o.value" :value="o.value" :label="o.label" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="关键词">
                        <el-input v-model="pageQuery.keyword" placeholder="标题/正文检索" clearable style="width: 180px" @keyup.enter="load" />
                    </el-form-item>
                    <el-form-item label="状态">
                        <el-select v-model="pageQuery.status" clearable placeholder="全部" style="width: 110px">
                            <el-option v-for="o in statusOptions" :key="o.value" :value="o.value" :label="o.label" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="load">查询</el-button>
                    </el-form-item>
                </el-form>
                <el-button type="primary" @click="openUpload">上传新政策</el-button>
            </div>

            <el-table :data="list" v-loading="loading" border stripe>
                <el-table-column prop="title" label="政策标题" min-width="280" show-overflow-tooltip />
                <el-table-column prop="issuingBody" label="发文机构" width="160" show-overflow-tooltip />
                <el-table-column label="分类" width="90">
                    <template #default="{ row }">
                        <el-tag size="small" :type="row.category === 'national' ? 'danger' : row.category === 'regional' ? 'warning' : 'primary'">
                            {{ categoryLabel(row.category) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="标签" min-width="150">
                    <template #default="{ row }">
                        <el-tag
                            v-for="t in (Array.isArray(row.tags) ? row.tags : typeof row.tags === 'string' ? JSON.parse(row.tags || '[]') : [])"
                            :key="t"
                            size="small"
                            style="margin-right: 4px"
                        >
                            {{ t }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="publishDate" label="发布日期" width="120">
                    <template #default="{ row }"><span class="mono">{{ (row.publishDate || '').slice(0, 10) }}</span></template>
                </el-table-column>
                <el-table-column label="状态" width="90">
                    <template #default="{ row }">
                        <el-tag
                            :type="row.status === 'published' ? 'success' : row.status === 'draft' ? 'info' : 'warning'"
                            size="small"
                        >
                            {{ statusOptions.find((o) => o.value === row.status)?.label || row.status }}
                        </el-tag>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination
                style="margin-top: 12px; justify-content: flex-end"
                layout="total, prev, pager, next"
                :total="total"
                :page-size="pageQuery.pageSize"
                :current-page="pageQuery.pageNum"
                @current-change="(p: number) => { pageQuery.pageNum = p; load(); }"
            />
        </el-card>

        <el-dialog v-model="dialogVisible" title="上传新政策" width="600px" destroy-on-close>
            <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
                <el-form-item label="政策标题" prop="title">
                    <el-input v-model="form.title" placeholder="如 关于开展现货市场连续运行的通知" />
                </el-form-item>
                <el-form-item label="发文机构">
                    <el-input v-model="form.issuingBody" placeholder="如 省发展改革委" />
                </el-form-item>
                <el-form-item label="政策分类" prop="category">
                    <el-select v-model="form.category" style="width: 100%">
                        <el-option v-for="o in categoryOptions" :key="o.value" :value="o.value" :label="o.label" />
                    </el-select>
                </el-form-item>
                <el-form-item label="标签">
                    <el-input v-model="form.tagsText" placeholder="逗号分隔，如 现货市场, 电价机制" />
                </el-form-item>
                <el-form-item label="发布日期">
                    <el-date-picker v-model="form.publishDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" placeholder="默认今天" />
                </el-form-item>
                <el-form-item label="生效日期">
                    <el-date-picker v-model="form.effectiveDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" placeholder="默认今天" />
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="form.status" style="width: 100%">
                        <el-option v-for="o in statusOptions" :key="o.value" :value="o.value" :label="o.label" />
                    </el-select>
                </el-form-item>
                <el-form-item label="文件地址">
                    <el-input v-model="form.fileUrl" placeholder="可选，minio://policy/upload/... 缺省自动生成" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submit">上传</el-button>
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
    }
    .mono {
        font-family: Consolas, monospace;
    }
}
</style>
